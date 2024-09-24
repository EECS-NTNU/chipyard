package chipyard

import freechips.rocketchip.config.{Config}

// ---------------------
// BOOM Configs
// ---------------------

class SmallBoomConfig extends Config(
  new boom.common.WithNSmallBooms(1) ++                          // small boom config
  new chipyard.config.AbstractConfig)

class MediumBoomConfig extends Config(
  new boom.common.WithNMediumBooms(1) ++                         // medium boom config
  new chipyard.config.AbstractConfig)

class MediumBoomConfigRenTaint extends Config(
  new boom.common.WithRenameTaintTrackingEnabled ++
  new boom.common.WithNMediumBooms(1) ++
  new chipyard.config.AbstractConfig)

class MediumBoomConfigRegTaint extends Config(
  new boom.common.WithRegisterTaintTrackingEnabled ++
  new boom.common.WithNMediumBooms(1) ++
  new chipyard.config.AbstractConfig)

class MediumBoomConfigNDA extends Config(
  new boom.common.WithNDAEnabled ++ 
  new boom.common.WithNMediumBooms(1) ++
  new chipyard.config.AbstractConfig)

class MediumBoomConfigStats extends Config(
  new boom.common.WithTraceStats ++
  new boom.common.WithNMediumBooms(1) ++                         // medium boom config
  new chipyard.config.AbstractConfig)

class MediumBoomConfigNDAStats extends Config(
  new boom.common.WithNDAEnabled ++ 
  new boom.common.WithTraceStats ++
  new boom.common.WithNMediumBooms(1) ++
  new chipyard.config.AbstractConfig)

class MediumBoomConfigRenTaintStats extends Config(
  new boom.common.WithRenameTaintTrackingEnabled ++
  new boom.common.WithTraceStats ++
  new boom.common.WithNMediumBooms(1) ++
  new chipyard.config.AbstractConfig)

class MediumBoomConfigRegTaintStats extends Config(
  new boom.common.WithRegisterTaintTrackingEnabled ++
  new boom.common.WithTraceStats ++
  new boom.common.WithNMediumBooms(1) ++
  new chipyard.config.AbstractConfig)

class MegaBoomConfigRegTaint extends Config(
  new boom.common.WithRegisterTaintTrackingEnabled ++
  new boom.common.WithNMegaBooms(1) ++
  new chipyard.config.AbstractConfig)

class MegaBoomConfigRenTaint extends Config(
  new boom.common.WithRenameTaintTrackingEnabled ++
  new boom.common.WithNMegaBooms(1) ++
  new chipyard.config.AbstractConfig)

class MegaBoomConfigNDA extends Config(
  new boom.common.WithNDAEnabled ++
  new boom.common.WithNMegaBooms(1) ++
  new chipyard.config.AbstractConfig)

class MegaBoomConfigStats extends Config(
  new boom.common.WithTraceStats ++
  new boom.common.WithNMegaBooms(1) ++                           // mega boom config
  new chipyard.config.AbstractConfig)

class MegaBoomConfigRegTaintStats extends Config(
  new boom.common.WithRegisterTaintTrackingEnabled ++ 
  new boom.common.WithTraceStats ++
  new boom.common.WithNMegaBooms(1) ++
  new chipyard.config.AbstractConfig)

class MegaBoomConfigRenTaintStats extends Config(
  new boom.common.WithRenameTaintTrackingEnabled ++ 
  new boom.common.WithTraceStats ++
  new boom.common.WithNMegaBooms(1) ++
  new chipyard.config.AbstractConfig)

class MegaBoomConfigNDAStats extends Config(
  new boom.common.WithNDAEnabled ++  
  new boom.common.WithTraceStats ++
  new boom.common.WithNMegaBooms(1) ++
  new chipyard.config.AbstractConfig)

class LargeBoomConfig extends Config(
  new boom.common.WithNLargeBooms(1) ++                          // large boom config
  new chipyard.config.AbstractConfig)

class MegaBoomConfig extends Config(
  new boom.common.WithNMegaBooms(1) ++                           // mega boom config
  new chipyard.config.AbstractConfig)

class DualSmallBoomConfig extends Config(
  new boom.common.WithNSmallBooms(2) ++                          // 2 boom cores
  new chipyard.config.AbstractConfig)

class HwachaLargeBoomConfig extends Config(
  new chipyard.config.WithHwachaTest ++
  new hwacha.DefaultHwachaConfig ++                              // use Hwacha vector accelerator
  new boom.common.WithNLargeBooms(1) ++
  new chipyard.config.AbstractConfig)

class LoopbackNICLargeBoomConfig extends Config(
  new chipyard.harness.WithLoopbackNIC ++                        // drive NIC IOs with loopback
  new icenet.WithIceNIC ++                                       // build a NIC
  new boom.common.WithNLargeBooms(1) ++
  new chipyard.config.AbstractConfig)

class DromajoBoomConfig extends Config(
  new chipyard.harness.WithSimDromajoBridge ++                   // attach Dromajo
  new chipyard.config.WithTraceIO ++                             // enable the traceio
  new boom.common.WithNSmallBooms(1) ++
  new chipyard.config.AbstractConfig)
