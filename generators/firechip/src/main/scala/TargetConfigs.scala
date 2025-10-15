package firesim.firesim

import java.io.File
import chisel3._
import chisel3.util.log2Up
import freechips.rocketchip.config.{Config, Parameters}
import freechips.rocketchip.groundtest.TraceGenParams
import freechips.rocketchip.tile._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.rocket.DCacheParams
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.tilelink.{BootROMLocated, BootROMParams}
import freechips.rocketchip.devices.debug.{DebugModuleKey, DebugModuleParams}
import freechips.rocketchip.diplomacy.{AsynchronousCrossing, LazyModule}
import testchipip.{BlockDeviceConfig, BlockDeviceKey, TracePortKey, TracePortParams}
import sifive.blocks.devices.uart.{PeripheryUARTKey, UARTParams}

import scala.math.{max, min}
import chipyard.clocking.ChipyardPRCIControlKey
import chipyard.config.WithTraceDoctorIO
import icenet._
import testchipip.WithRingSystemBus
import firesim.bridges._
import firesim.configs._

class WithBootROM extends Config((site, here, up) => {
  case BootROMLocated(x) => {
    val chipyardBootROM = new File(s"./generators/testchipip/bootrom/bootrom.rv${site(XLen)}.img")
    val firesimBootROM = new File(s"./target-rtl/chipyard/generators/testchipip/bootrom/bootrom.rv${site(XLen)}.img")

    val bootROMPath = if (chipyardBootROM.exists()) {
      chipyardBootROM.getAbsolutePath()
    } else {
      firesimBootROM.getAbsolutePath()
    }
    up(BootROMLocated(x), site).map(_.copy(contentFileName = bootROMPath))
  }
})

// Disables clock-gating; doesn't play nice with our FAME-1 pass
class WithoutClockGating extends Config((site, here, up) => {
  case DebugModuleKey => up(DebugModuleKey, site).map(_.copy(clockGate = false))
  case ChipyardPRCIControlKey => up(ChipyardPRCIControlKey, site).copy(enableTileClockGating = false)
})

// Testing configurations
// This enables printfs used in testing
class WithScalaTestFeatures extends Config((site, here, up) => {
  case TracePortKey => up(TracePortKey, site).map(_.copy(print = true))
})

// FASED Config Aliases. This to enable config generation via "_" concatenation
// which requires that all config classes be defined in the same package
class DDR3FCFS extends FCFS16GBQuadRank
class DDR3FRFCFS extends FRFCFS16GBQuadRank
class DDR3FRFCFSLLC4MB extends FRFCFS16GBQuadRankLLC4MB

class WithNIC extends icenet.WithIceNIC(inBufFlits = 8192, ctrlQueueDepth = 64)

// Adds a small/large NVDLA to the system
class WithNVDLALarge extends nvidia.blocks.dla.WithNVDLA("large")
class WithNVDLASmall extends nvidia.blocks.dla.WithNVDLA("small")

// Non-frequency tweaks that are generally applied to all firesim configs
class WithFireSimDesignTweaks extends Config(
  // Optional: reduce the width of the Serial TL interface
  new testchipip.WithSerialTLWidth(4) ++
  // Required: Bake in the default FASED memory model
  new WithDefaultMemModel ++
  // Required*: Uses FireSim ClockBridge and PeekPokeBridge to drive the system with a single clock/reset
  new WithFireSimHarnessClockBinder ++
  new WithFireSimSimpleClocks ++
  // Required*: When using FireSim-as-top to provide a correct path to the target bootrom source
  new WithBootROM ++
  // Required: Existing FAME-1 transform cannot handle black-box clock gates
  new WithoutClockGating ++
  // Required*: Removes thousands of assertions that would be synthesized (* pending PriorityMux bugfix)
  new WithoutTLMonitors ++
  // Optional: Adds IO to attach tracerV bridges
  new chipyard.config.WithTraceIO ++
  new chipyard.config.WithTraceDoctorIO ++
  // Optional: Request 16 GiB of target-DRAM by default (can safely request up to 32 GiB on F1)
  new freechips.rocketchip.subsystem.WithExtMemSize((1 << 30) * 16L) ++
  // Optional: Removing this will require using an initramfs under linux
  new testchipip.WithBlockDevice ++
  // Required*: Scale default baud rate with periphery bus frequency
  new chipyard.config.WithUART(BigInt(3686400L)) ++
  // Required: Do not support debug module w. JTAG until FIRRTL stops emitting @(posedge ~clock)
  new chipyard.config.WithNoDebug
)

// Tweaks to modify target clock frequencies / crossings to legacy firesim defaults
class WithFireSimHighPerfClocking extends Config(
  // Optional: This sets the default frequency for all buses in the system to 3.2 GHz
  // (since unspecified bus frequencies will use the pbus frequency)
  // This frequency selection matches FireSim's legacy selection and is required
  // to support 200Gb NIC performance. You may select a smaller value.
  new chipyard.config.WithPeripheryBusFrequency(3200.0) ++
  // Optional: These three configs put the DRAM memory system in it's own clock domain.
  // Removing the first config will result in the FASED timing model running
  // at the pbus freq (above, 3.2 GHz), which is outside the range of valid DDR3 speedgrades.
  // 1 GHz matches the FASED default, using some other frequency will require
  // runnings the FASED runtime configuration generator to generate faithful DDR3 timing values.
  new chipyard.config.WithMemoryBusFrequency(1000.0) ++
  new chipyard.config.WithAsynchrousMemoryBusCrossing ++
  new testchipip.WithAsynchronousSerialSlaveCrossing
)

// Tweaks that are generally applied to all firesim configs setting a single clock domain at 1000 MHz
class WithFireSimConfigTweaks extends Config(
  // 1 GHz matches the FASED default (DRAM modeli realistically configured for that frequency)
  // Using some other frequency will require runnings the FASED runtime configuration generator
  // to generate faithful DDR3 timing values.
  new chipyard.config.WithSystemBusFrequency(1000.0) ++
  new chipyard.config.WithSystemBusFrequencyAsDefault ++ // All unspecified clock frequencies, notably the implicit clock, will use the sbus freq (1000 MHz)
  // Explicitly set PBUS + MBUS to 1000 MHz, since they will be driven to 100 MHz by default because of assignments in the Chisel
  new chipyard.config.WithPeripheryBusFrequency(1000.0) ++
  new chipyard.config.WithMemoryBusFrequency(1000.0) ++
  new WithFireSimDesignTweaks
)

// Tweak more representative of testchip configs
class WithFireSimTestChipConfigTweaks extends Config(
  new chipyard.config.WithTestChipBusFreqs ++
  new WithFireSimDesignTweaks
)

// Tweaks for legacy FireSim configs.
class WithFireSimHighPerfConfigTweaks extends Config(
  new WithFireSimHighPerfClocking ++
  new WithFireSimDesignTweaks
)

/*******************************************************************************
* Full TARGET_CONFIG configurations. These set parameters of the target being
* simulated.
*
* In general, if you're adding or removing features from any of these, you
* should CREATE A NEW ONE, WITH A NEW NAME. This is because the manager
* will store this name as part of the tags for the AGFI, so that later you can
* reconstruct what is in a particular AGFI. These tags are also used to
* determine which driver to build.
 *******************************************************************************/

//*****************************************************************
// Rocket configs, base off chipyard's RocketConfig
//*****************************************************************
// DOC include start: firesimconfig
class FireSimRocketConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.RocketConfig)
// DOC include end: firesimconfig

class FireSimQuadRocketConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.QuadRocketConfig)

// A stripped down configuration that should fit on all supported hosts.
// Flat to avoid having to reorganize the config class hierarchy to remove certain features
class FireSimSmallSystemConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithBootROM ++
  new chipyard.config.WithPeripheryBusFrequency(3200.0) ++
  new WithoutClockGating ++
  new WithoutTLMonitors ++
  new freechips.rocketchip.subsystem.WithExtMemSize(1 << 28) ++
  new testchipip.WithDefaultSerialTL ++
  new testchipip.WithBlockDevice ++
  new chipyard.config.WithUART ++
  new freechips.rocketchip.subsystem.WithInclusiveCache(nWays = 2, capacityKB = 64) ++
  new chipyard.RocketConfig)


//*****************************************************************
// Boom config, base off chipyard's SmallBoomConfig
//*****************************************************************
class FireSimSmallBoomConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.SmallBoomConfig)

//*****************************************************************
// Boom config, base off chipyard's LargeBoomConfig
//*****************************************************************
class FireSimLargeBoomConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.LargeBoomConfig)

//********************************************************************
// Heterogeneous config, base off chipyard's LargeBoomAndRocketConfig
//********************************************************************
class FireSimLargeBoomAndRocketConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.LargeBoomAndRocketConfig)

//******************************************************************
// Gemmini NN accel config, base off chipyard's GemminiRocketConfig
//******************************************************************
class FireSimGemminiRocketConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.GemminiRocketConfig)

//******************************************************************
// Configuration with Ring topology SystemBus
//******************************************************************
class FireSimRingSystemBusRocketConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.RingSystemBusRocketConfig)

//**********************************************************************************
// Supernode Configurations, base off chipyard's RocketConfig
//**********************************************************************************
class SupernodeFireSimRocketConfig extends Config(
  new WithNumNodes(4) ++
  new freechips.rocketchip.subsystem.WithExtMemSize((1 << 30) * 8L) ++ // 8 GB
  new FireSimRocketConfig)

//**********************************************************************************
//* CVA6 Configurations
//*********************************************************************************/
class FireSimCVA6Config extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new chipyard.CVA6Config)

//**********************************************************************************
//* Multiclock Configurations
//*********************************************************************************/
class FireSimMulticlockAXIOverSerialConfig extends Config(
  new WithAXIOverSerialTLCombinedBridges ++ // use combined bridge to connect to axi mem over serial
  new WithDefaultFireSimBridges ++
  new testchipip.WithBlockDevice(false) ++ // disable blockdev
  new WithDefaultMemModel ++
  new WithFireSimDesignTweaks ++ // don't inherit firesim clocking
  new chipyard.MulticlockAXIOverSerialConfig
)

//**********************************************************************************
// System with 16 LargeBOOMs that can be simulated with Golden Gate optimizations
// - Requires MTModels and MCRams mixins as prefixes to the platform config
// - May require larger build instances or JVM memory footprints
//*********************************************************************************/
class FireSim16LargeBoomConfig extends Config(
  new WithDefaultFireSimBridges ++
  new WithDefaultMemModel ++
  new WithFireSimConfigTweaks ++
  new boom.common.WithNLargeBooms(16) ++
  new chipyard.config.AbstractConfig)

class FireSimNoMemPortConfig extends Config(
  new WithDefaultFireSimBridges ++
  new freechips.rocketchip.subsystem.WithNoMemPort ++
  new testchipip.WithBackingScratchpad ++
  new WithFireSimConfigTweaks ++
  new chipyard.RocketConfig)

/**
  * Custom config for Vortex, as there are some configs within WithFireSimDesignTweaks
  * that either break the compilation or is not needed.
  *
  */
class WithFireSimVortexTweaks extends Config(
  new WithDefaultFireSimBridges ++
  // 1 GHz matches the FASED default (DRAM modeli realistically configured for that frequency)
  // Using some other frequency will require runnings the FASED runtime configuration generator
  // to generate faithful DDR3 timing values.
  new chipyard.config.WithSystemBusFrequency(1000.0) ++
  new chipyard.config.WithSystemBusFrequencyAsDefault ++ // All unspecified clock frequencies, notably the implicit clock, will use the sbus freq (1000 MHz)
  // Explicitly set PBUS + MBUS to 1000 MHz, since they will be driven to 100 MHz by default because of assignments in the Chisel
  new chipyard.config.WithPeripheryBusFrequency(1000.0) ++
  new chipyard.config.WithMemoryBusFrequency(1000.0) ++
  // Optional: reduce the width of the Serial TL interface
  new testchipip.WithSerialTLWidth(4) ++
  // Required: Bake in the default FASED memory model
  new WithDefaultMemModel ++
  // Required*: Uses FireSim ClockBridge and PeekPokeBridge to drive the system with a single clock/reset
  new WithFireSimHarnessClockBinder ++
  new WithFireSimSimpleClocks ++
  // Required*: When using FireSim-as-top to provide a correct path to the target bootrom source
  new WithBootROM ++
  // Required: Existing FAME-1 transform cannot handle black-box clock gates
  new WithoutClockGating ++
  // Required*: Removes thousands of assertions that would be synthesized (* pending PriorityMux bugfix)
  new WithoutTLMonitors ++
  // Optional: bridge for tracerv
  //new chipyard.config.WithTraceIO ++
  // TODO: add trace doctor
  new chipyard.config.WithTraceDoctorIO(2) ++
  // Required*: Scale default baud rate with periphery bus frequency
  new chipyard.config.WithUART(BigInt(3686400L)) ++
  // Required: Do not support debug module w. JTAG until FIRRTL stops emitting @(posedge ~clock)
  new chipyard.config.WithNoDebug
)

class FireSimVortexGPGPUConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.VortexGPGPUConfig
)

class FireSimSuperSmallVortexConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.SuperSmallVortexConfig
)

class FireSimXtraSmallVortexConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.XtraSmallVortexConfig
)

class FireSimSmallVortexConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.SmallVortexConfig
)

class FireSimMediumVortexConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.MediumVortexConfig
)

class FireSimLargeVortexConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.LargeVortexConfig
)

class FireSimXtraLargeVortexConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.XtraLargeVortexConfig
)

class FireSimSuperLargeVortexConfig extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.SuperLargeVortexConfig
)

// Firesim configs with specific numbers of SMs and Clusters.

class FireSimV1S1C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V1S1C
)
class FireSimV2S1C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V2S1C
)
class FireSimV4S1C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V4S1C
)
class FireSimV8S1C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V8S1C
)
class FireSimV16S1C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V16S1C
)
class FireSimV32S1C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V32S1C
)
class FireSimV1S2C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V1S2C
)
class FireSimV2S2C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V2S2C
)
class FireSimV4S2C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V4S2C
)
class FireSimV8S2C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V8S2C
)
class FireSimV16S2C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V16S2C
)
class FireSimV1S4C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V1S4C
)
class FireSimV2S4C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V2S4C
)
class FireSimV4S4C extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V4S4C
)

class FireSimV1S1C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V1S1C0L3
)
class FireSimV2S1C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V2S1C0L3
)
class FireSimV4S1C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V4S1C0L3
)
class FireSimV8S1C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V8S1C0L3
)
class FireSimV16S1C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V16S1C0L3
)
class FireSimV32S1C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V32S1C0L3
)
class FireSimV1S2C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V1S2C0L3
)
class FireSimV2S2C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V2S2C0L3
)
class FireSimV4S2C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V4S2C0L3
)
class FireSimV8S2C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V8S2C0L3
)
class FireSimV16S2C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V16S2C0L3
)
class FireSimV1S4C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V1S4C0L3
)
class FireSimV2S4C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V2S4C0L3
)
class FireSimV4S4C0L3 extends Config(
  new WithFireSimVortexTweaks ++
  new chipyard.V4S4C0L3
)
