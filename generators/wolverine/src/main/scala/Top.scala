package wolverine

import chipsalliance.rocketchip.config.{Config, Parameters}
import chipyard.{Subsystem, SubsystemModuleImp}
import chisel3._
import chisel3.experimental.IO
import chisel3.util._
import freechips.rocketchip.amba.axi4.{AXI4Bundle, AXI4BundleParameters, AXI4Fragmenter, AXI4MasterNode, AXI4MasterParameters, AXI4MasterPortParameters, AXI4RegBundle, AXI4RegModule, AXI4RegisterRouter}
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.devices.debug.{HasPeripheryDebug, HasPeripheryDebugModuleImp}
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.diplomacy.{IdRange, LazyModule, LazyModuleImp}
import freechips.rocketchip.regmapper.RegField
import freechips.rocketchip.subsystem._
import freechips.rocketchip.system.{ExampleRocketSystem, SimAXIMem}
import freechips.rocketchip.util.DontTouch
import testchipip._
import utilities._
import zynq._

// command (request) bundle for memory read/writes
class ConveyMemRequest(rtnCtlBits: Int, addrBits: Int, dataBits: Int) extends Bundle {
  val rtnCtl      = UInt(rtnCtlBits.W)
  val writeData   = UInt(dataBits.W)
  val addr        = UInt(addrBits.W)
  val size        = UInt(2.W)
  val cmd         = UInt(3.W)
  val scmd        = UInt(4.W)

//  override def clone = {
//    new ConveyMemRequest(rtnCtlBits, addrBits, dataBits).asInstanceOf[this.type] }
}

// response bundle for return read data or write completes (?)
class ConveyMemResponse(val rtnCtlBits: Int, val dataBits: Int) extends Bundle {
  val rtnCtl      = UInt(rtnCtlBits.W)
  val readData    = UInt(dataBits.W)
  val cmd         = UInt(3.W)
  val scmd        = UInt(4.W)

//  override def clone = {
//    new ConveyMemResponse(rtnCtlBits, dataBits).asInstanceOf[this.type] }
}

// memory port master interface
class ConveyMemMasterIF(rtnCtlBits: Int) extends Bundle {
  // note that req and rsp are defined by Convey as stall/valid interfaces
  // (instead of ready/valid as defined here) -- needs adapter
  val req         = Decoupled(new ConveyMemRequest(rtnCtlBits, 48, 64))
  val rsp         = Flipped(Decoupled(new ConveyMemResponse(rtnCtlBits, 64)))
  val flushReq    = Output(Bool())
  val flushOK     = Input(Bool())

  override def clone = {
    new ConveyMemMasterIF(rtnCtlBits).asInstanceOf[this.type] }
}

// wrapper for Convey Verilog personality
class TopWrapperInterface(numMemPorts: Int, rtnctl: Int) extends Bundle {
  // dispatch interface
  val dispInstValid = Input(Bool())
  val dispInstData  = Input(UInt(5.W))
  val dispRegID     = Input(UInt(18.W))
  val dispRegRead   = Input(Bool())
  val dispRegWrite  = Input(Bool())
  val dispRegWrData = Input(UInt(64.W))
  val dispAegCnt    = Output(UInt(18.W))
  val dispException = Output(UInt(16.W))
  val dispIdle      = Output(Bool())
  val dispRtnValid  = Output(Bool())
  val dispRtnData   = Output(UInt(64.W))
  val dispStall     = Output(Bool())
  // memory controller interface
  // request
  val mcReqValid    = Output(UInt((numMemPorts).W))
  val mcReqRtnCtl   = Output(UInt((rtnctl*numMemPorts).W))
  val mcReqData     = Output(UInt((64*numMemPorts).W))
  val mcReqAddr     = Output(UInt((48*numMemPorts).W))
  val mcReqSize     = Output(UInt((2*numMemPorts).W))
  val mcReqCmd      = Output(UInt((3*numMemPorts).W))
  val mcReqSCmd     = Output(UInt((4*numMemPorts).W))
  val mcReqStall    = Input(UInt((numMemPorts).W))
  // response
  val mcResValid    = Input(UInt((numMemPorts).W))
  val mcResCmd      = Input(UInt((3*numMemPorts).W))
  val mcResSCmd     = Input(UInt((4*numMemPorts).W))
  val mcResData     = Input(UInt((64*numMemPorts).W))
  val mcResRtnCtl   = Input(UInt((rtnctl*numMemPorts).W))
  val mcResStall    = Output(UInt((numMemPorts).W))
  // flush
  val mcReqFlush    = Output(UInt((numMemPorts).W))
  val mcResFlushOK  = Input(UInt((numMemPorts).W))
  // control-status register interface
  val csrWrValid      = Input(Bool())
  val csrRdValid      = Input(Bool())
  val csrAddr         = Input(UInt(16.W))
  val csrWrData       = Input(UInt(64.W))
  val csrReadAck      = Output(Bool())
  val csrReadData     = Output(UInt(64.W))
  // misc
  val aeid            = Input(UInt(4.W))


  // rename signals
  def renameSignals() {
    dispInstValid.suggestName("disp_inst_vld")
    dispInstData.suggestName("disp_inst")
    dispRegID.suggestName("disp_aeg_idx")
    dispRegRead.suggestName("disp_aeg_rd")
    dispRegWrite.suggestName("disp_aeg_wr")
    dispRegWrData.suggestName("disp_aeg_wr_data")
    dispAegCnt.suggestName("disp_aeg_cnt")
    dispException.suggestName("disp_exception")
    dispIdle.suggestName("disp_idle")
    dispRtnValid.suggestName("disp_rtn_data_vld")
    dispRtnData.suggestName("disp_rtn_data")
    dispStall.suggestName("disp_stall")
    mcReqValid.suggestName("mc_rq_vld")
    mcReqRtnCtl.suggestName("mc_rq_rtnctl")
    mcReqData.suggestName("mc_rq_data")
    mcReqAddr.suggestName("mc_rq_vadr")
    mcReqSize.suggestName("mc_rq_size")
    mcReqCmd.suggestName("mc_rq_cmd")
    mcReqSCmd.suggestName("mc_rq_scmd")
    mcReqStall.suggestName("mc_rq_stall")
    mcResValid.suggestName("mc_rs_vld")
    mcResCmd.suggestName("mc_rs_cmd")
    mcResSCmd.suggestName("mc_rs_scmd")
    mcResData.suggestName("mc_rs_data")
    mcResRtnCtl.suggestName("mc_rs_rtnctl")
    mcResStall.suggestName("mc_rs_stall")
    mcReqFlush.suggestName("mc_rq_flush")
    mcResFlushOK.suggestName("mc_rs_flush_cmplt")
    csrWrValid.suggestName("csr_wr_vld")
    csrRdValid.suggestName("csr_rd_vld")
    csrAddr.suggestName("csr_address")
    csrWrData.suggestName("csr_wr_data")
    csrReadAck.suggestName("csr_rd_ack")
    csrReadData.suggestName("csr_rd_data")
    aeid.suggestName("i_aeid")
  }
}

class AxiTest(address: BigInt, idBits: Int = 0, beatBytes: Int = 4)(implicit p: Parameters)
  extends LazyModule {
    val node = AXI4MasterNode(Seq(AXI4MasterPortParameters(
    masters = Seq(AXI4MasterParameters(
      name = "Axi Test",
      id = IdRange(0, 1 << idBits))))))

  val core = LazyModule(new AXI4RegisterRouter(
    address, beatBytes = beatBytes, concurrency = 1)(
    new AXI4RegBundle((), _){

    })(
    new AXI4RegModule((), _, _) {
      val regCnt = 8
      val regs = Reg(Vec(regCnt, UInt(32.W)))
      regs(0) := regs(0) + 1.U
      regmap(
        0x00 -> Seq(RegField(32, regs(0))),
        0x04 -> Seq(RegField(32, regs(1))),
        0x08 -> Seq(RegField(32, regs(2))),
        0x0C -> Seq(RegField(32, regs(3))),
        0x10 -> Seq(RegField(32, regs(4))),
        0x14 -> Seq(RegField(32, regs(5))),
        0x18 -> Seq(RegField(32, regs(6))),
        0x1C -> Seq(RegField(32, regs(7))),
      )
    }))
  core.node := AXI4Fragmenter() := node
  lazy val module = new LazyModuleImp(this){
    val axi = IO(Flipped(node.out(0)._1.cloneType))
    node.out(0)._1 <> axi
  }
}

class WolverineTop(implicit val p: Parameters) extends RawModule {
  val numMemPorts: Int = 1
  val idBits: Int = 32
  val clock = IO(Input(Clock()))
  val clockHalf = IO(Input(Clock()))
  val clockDouble = IO(Input(Clock()))
  val reset = IO(Input(Bool()))
  // dispatch interface
  val dispInstValid = IO(Input(Bool()))
  val dispInstData  = IO(Input(UInt(5.W)))
  val dispRegID     = IO(Input(UInt(18.W)))
  val dispRegRead   = IO(Input(Bool()))
  val dispRegWrite  = IO(Input(Bool()))
  val dispRegWrData = IO(Input(UInt(64.W)))
  val dispAegCnt    = IO(Output(UInt(18.W)))
  val dispException = IO(Output(UInt(16.W)))
  val dispIdle      = IO(Output(Bool()))
  val dispRtnValid  = IO(Output(Bool()))
  val dispRtnData   = IO(Output(UInt(64.W)))
  val dispStall     = IO(Output(Bool()))
  // memory controller interface
  // request
  val mcReqValid    = IO(Output(UInt((numMemPorts).W)))
  val mcReqRtnCtl   = IO(Output(UInt((idBits*numMemPorts).W)))
  val mcReqData     = IO(Output(UInt((64*numMemPorts).W)))
  val mcReqAddr     = IO(Output(UInt((48*numMemPorts).W)))
  val mcReqSize     = IO(Output(UInt((2*numMemPorts).W)))
  val mcReqCmd      = IO(Output(UInt((3*numMemPorts).W)))
  val mcReqSCmd     = IO(Output(UInt((4*numMemPorts).W)))
  val mcReqStall    = IO(Input(UInt((numMemPorts).W)))
  // response
  val mcResValid    = IO(Input(UInt((numMemPorts).W)))
  val mcResCmd      = IO(Input(UInt((3*numMemPorts).W)))
  val mcResSCmd     = IO(Input(UInt((4*numMemPorts).W)))
  val mcResData     = IO(Input(UInt((64*numMemPorts).W)))
  val mcResRtnCtl   = IO(Input(UInt((idBits*numMemPorts).W)))
  val mcResStall    = IO(Output(UInt((numMemPorts).W)))
  // flush
  val mcReqFlush    = IO(Output(UInt((numMemPorts).W)))
  val mcResFlushOK  = IO(Input(UInt((numMemPorts).W)))
  // control-status register interface
  val csrWrValid      = IO(Input(Bool()))
  val csrRdValid      = IO(Input(Bool()))
  val csrAddr         = IO(Input(UInt(16.W)))
  val csrWrData       = IO(Input(UInt(64.W)))
  val csrReadAck      = IO(Output(Bool()))
  val csrReadData     = IO(Output(UInt(64.W)))
  // misc
  val aeid            = IO(Input(UInt(4.W)))

  // rename signals
  def renameSignals() {
    clock.suggestName("clk")
    clockHalf.suggestName("clkhx")
    clockDouble.suggestName("clk2x")
    reset.suggestName("i_reset")
    dispInstValid.suggestName("disp_inst_vld")
    dispInstData.suggestName("disp_inst")
    dispRegID.suggestName("disp_aeg_idx")
    dispRegRead.suggestName("disp_aeg_rd")
    dispRegWrite.suggestName("disp_aeg_wr")
    dispRegWrData.suggestName("disp_aeg_wr_data")
    dispAegCnt.suggestName("disp_aeg_cnt")
    dispException.suggestName("disp_exception")
    dispIdle.suggestName("disp_idle")
    dispRtnValid.suggestName("disp_rtn_data_vld")
    dispRtnData.suggestName("disp_rtn_data")
    dispStall.suggestName("disp_stall")
    mcReqValid.suggestName("mc_rq_vld")
    mcReqRtnCtl.suggestName("mc_rq_rtnctl")
    mcReqData.suggestName("mc_rq_data")
    mcReqAddr.suggestName("mc_rq_vadr")
    mcReqSize.suggestName("mc_rq_size")
    mcReqCmd.suggestName("mc_rq_cmd")
    mcReqSCmd.suggestName("mc_rq_scmd")
    mcReqStall.suggestName("mc_rq_stall")
    mcResValid.suggestName("mc_rs_vld")
    mcResCmd.suggestName("mc_rs_cmd")
    mcResSCmd.suggestName("mc_rs_scmd")
    mcResData.suggestName("mc_rs_data")
    mcResRtnCtl.suggestName("mc_rs_rtnctl")
    mcResStall.suggestName("mc_rs_stall")
    mcReqFlush.suggestName("mc_rq_flush")
    mcResFlushOK.suggestName("mc_rs_flush_cmplt")
    csrWrValid.suggestName("csr_wr_vld")
    csrRdValid.suggestName("csr_rd_vld")
    csrAddr.suggestName("csr_address")
    csrWrData.suggestName("csr_wr_data")
    csrReadAck.suggestName("csr_rd_ack")
    csrReadData.suggestName("csr_rd_data")
    aeid.suggestName("i_aeid")
  }
  withClockAndReset(clock, reset) {
    // registers
    val regCnt = 8
    val regs = Reg(Vec(regCnt, UInt(64.W)))

    val stall_reg = regs(0)

    val dispReadAddr = RegEnable(dispRegID, dispRegRead)
    dispRtnValid := RegNext(dispRegRead)
    dispRtnData := regs(dispReadAddr)
    when(dispRegWrite) {
      regs(dispRegID) := dispRegWrData
    }
    // csr
    val csrReadAddr = RegEnable(csrAddr, csrRdValid)
    csrReadData := regs(csrReadAddr)
    csrReadAck := RegNext(csrRdValid)
    when(csrWrValid && csrAddr<regCnt.U){
      regs(csrAddr) := csrWrData
    }
    // TODO: react to dispatch
    dispIdle := !stall_reg.orR()
    dispStall := dispInstValid || stall_reg.orR()
    when(dispInstValid) {
      stall_reg := 1.U
    }
    dispException := Cat(
      (dispRegWrite || dispRegRead) && (dispRegID >= regCnt.U),
      dispInstValid && dispInstData =/= 0.U
    )
    dispAegCnt := regCnt.U

//    val p: Parameters = new With1GbRam ++ new RocketZynqConfig ++ Parameters.empty
    val memAxiIdBits = 4

    val top = Module(new Top()(p))
    val adapt_axi = top.io.ps_axi_slave
    val mem_axi = top.io.mem_axi
    top.io.fan_rpm := 0.U
//    val axiTest = Module(LazyModule(new AxiTest(address = p(ZynqAdapterBase))(p)).module)
//    val adapt_axi = axiTest.axi
//    val mem_axi = Wire(Flipped(new AXI4Bundle(new AXI4BundleParameters(32, 64, memAxiIdBits))))

    val sIDLE :: sREAD :: sWRITE :: sWRITE_DATA :: sREAD_DATA :: sWRITE_RESPONSE :: Nil = Enum(6)

    val eOK :: eBURST :: eLEN :: eSIZE :: eQUEUE_READY :: eQUEUE_CMD :: Nil = Enum(6)

    val mem_axi_error_reg = RegInit(eOK)
    //TODO: allocate memory
    val mem_alloc_addr = regs(1)

    val mem_ram_base = p(ExtMem).get.master.base.U
    val mem_write_addr = RegInit(0.U)
    val mem_write_id = RegInit(0.U)
    val mem_request_state = RegInit(sIDLE)

    val adapt_addr = RegInit(0.U)
    val adapt_wData = RegInit(0.U)
    val adapt_rData = RegInit(0.U)
    val adapt_state = RegInit(sIDLE)

    when(csrReadAck && csrReadAddr === regCnt.U){
      csrReadData := adapt_rData
    }
    when(csrReadAck && csrReadAddr === (regCnt+1).U){
      csrReadData := adapt_addr
    }
    when(csrReadAck && csrReadAddr === (regCnt+2).U){
      csrReadData := adapt_wData
    }
    when(csrReadAck && csrReadAddr === (regCnt+3).U){
      csrReadData := adapt_rData
    }
    when(csrReadAck && csrReadAddr === (regCnt+4).U){
      csrReadData := adapt_state
    }
    when(csrReadAck && csrReadAddr === (regCnt+5).U){
      csrReadData := mem_axi_error_reg
    }

    // this is quite hacky and only works because csr operations only happen every ~100 cycles at 50MHz
    when(csrAddr === regCnt.U && csrWrValid){
        val write = csrWrData(63)
        adapt_addr := csrWrData(62, 32)
        when(write){
          adapt_state := sWRITE
          adapt_wData := csrWrData(31, 0)
        } .otherwise{
          adapt_state := sREAD
        }
    }

    //read add
    adapt_axi.ar.bits.id := 0.U
    adapt_axi.ar.bits.addr := adapt_addr
    adapt_axi.ar.bits.len := 0.U // 1 transfer
    adapt_axi.ar.bits.size := "b010".U // 4 bytes per transfer
    adapt_axi.ar.bits.burst := "b00".U // FIXED
    adapt_axi.ar.bits.lock := 0.U // no lock?
    adapt_axi.ar.bits.cache := 0.U // no buffer?
    adapt_axi.ar.bits.prot := 0.U // unpriviledged, secure, data?
    adapt_axi.ar.bits.qos := 0.U // no qos
    //write addr
    adapt_axi.aw.bits.id := 0.U
    adapt_axi.aw.bits.addr := adapt_addr
    adapt_axi.aw.bits.len := 0.U // 1 transfer
    adapt_axi.aw.bits.size := "b010".U // 4 bytes per transfer
    adapt_axi.aw.bits.burst := "b00".U // FIXED
    adapt_axi.aw.bits.lock := 0.U // no lock?
    adapt_axi.aw.bits.cache := 0.U // no buffer?
    adapt_axi.aw.bits.prot := 0.U // unpriviledged, secure, data?
    adapt_axi.aw.bits.qos := 0.U // no qos
    //write data
    adapt_axi.w.bits.data := adapt_wData
    adapt_axi.w.bits.strb := ((1<<4)-1).U
    adapt_axi.w.bits.last := true.B

    //send/receive nothing
    adapt_axi.ar.valid := false.B
    adapt_axi.aw.valid := false.B
    adapt_axi.w.valid := false.B
    adapt_axi.r.ready := false.B
    adapt_axi.b.ready := false.B

    when(adapt_state === sREAD){
      adapt_axi.ar.valid := true.B
      when(adapt_axi.ar.fire()){
        adapt_state := sREAD_DATA
      }
    }.elsewhen(adapt_state === sWRITE){
      adapt_axi.aw.valid := true.B
      adapt_axi.w.valid := true.B

      when(adapt_axi.aw.fire() && adapt_axi.w.fire()){
        adapt_state := sWRITE_RESPONSE
      }.elsewhen(adapt_axi.aw.fire()){
        adapt_state := sWRITE_DATA
      }
    }.elsewhen(adapt_state === sWRITE_DATA){
      adapt_axi.w.valid := true.B
      when(adapt_axi.w.fire()){
        adapt_state := sWRITE_RESPONSE
      }
    }.elsewhen(adapt_state === sREAD_DATA){
      adapt_axi.r.ready := true.B
      when(adapt_axi.r.fire()){
        adapt_rData := adapt_axi.r.bits.data
        adapt_state := sIDLE
      }
    }.elsewhen(adapt_state === sWRITE_RESPONSE){
      adapt_axi.b.ready := true.B
      when(adapt_axi.b.fire()){
        adapt_state := sIDLE
      }
    }

    // mc
    mcReqValid := false.B
    mcReqCmd := 0.U
    mcReqSCmd := 0.U
    mcReqSize := 0.U
    mcReqAddr := 0.U
    mcReqRtnCtl := 0.U
    mcReqData := 0.U
    mcReqFlush := false.B

    mcResStall := false.B


    // supports only 64byte transactions for now
    // ensure correct values
    when(mem_axi.ar.valid){
      when(mem_axi.ar.bits.burst =/= "b01".U /*INC*/){
        mem_axi_error_reg := eBURST
      }.elsewhen(mem_axi.ar.bits.len =/= 7.U /*8 beats*/){
        mem_axi_error_reg := eLEN
      }.elsewhen(mem_axi.ar.bits.size =/= "b011".U /*8 bytes*/){
        mem_axi_error_reg := eSIZE
      }
    }

    // closed by default
    mem_axi.tieoff()

    val can_request = !RegNext(mcReqStall(0))

    // handles ar, aw and w
    when(mem_request_state === sIDLE){
      when(mem_axi.ar.valid){
        mem_request_state := sREAD
      }.elsewhen(mem_axi.aw.valid){
        mem_request_state := sWRITE
      }
    }.elsewhen(mem_request_state === sREAD){
      mem_axi.ar.ready := can_request
      when(mem_axi.ar.fire()){
        mem_request_state := sIDLE
        mcReqValid := true.B
        mcReqCmd := 7.U // multi-quadword read
        mcReqSCmd := 0.U
        mcReqSize := 3.U
        mcReqAddr := mem_alloc_addr + mem_axi.ar.bits.addr - mem_ram_base
        mcReqRtnCtl := Cat(0.U(1.W), mem_axi.ar.bits.id)
      }
    }.elsewhen(mem_request_state === sWRITE){
      mem_axi.aw.ready := can_request
      when(mem_axi.aw.fire()){
        mem_request_state := sWRITE_DATA
        mem_write_addr := mem_axi.aw.bits.addr
        mem_write_id := mem_axi.aw.bits.id
      }
    }.elsewhen(mem_request_state === sWRITE_DATA){
      mem_axi.w.ready := can_request
      when(mem_axi.w.fire()){
        mcReqValid := true.B
        mcReqCmd := 6.U // multi-quadword write
        mcReqSCmd := 0.U // 8 beats (64bytes)
        mcReqSize := 3.U
        mcReqAddr := mem_alloc_addr + mem_write_addr - mem_ram_base
        mcReqRtnCtl := Cat(1.U(1.W), mem_write_id)
        when(mem_axi.w.bits.last){
          mem_request_state := sIDLE
        }
      }
    }
    // handles r and b

    //response fifo
    val response_queue = Module(new Queue(new ConveyMemResponse(rtnCtlBits = memAxiIdBits+1, 64), 16, false, true))
    response_queue.io.enq.valid := mcResValid(0)
    mcResStall := response_queue.io.count >= 8.U
    // ensure that queue logic works
    when(response_queue.io.enq.valid && !response_queue.io.enq.ready){
      mem_axi_error_reg := eQUEUE_READY
    }
    response_queue.io.enq.bits.cmd := mcResCmd
    response_queue.io.enq.bits.scmd := mcResSCmd
    response_queue.io.enq.bits.rtnCtl := mcResRtnCtl
    response_queue.io.enq.bits.readData := mcResData

    response_queue.io.deq.ready := false.B

    mem_axi.b.bits.id := 0.U
    mem_axi.b.bits.resp := 0.U
    mem_axi.r.bits.id := 0.U
    mem_axi.r.bits.last := 0.U
    mem_axi.r.bits.resp := 0.U
    mem_axi.r.bits.data := 0.U

    when(response_queue.io.deq.valid){
      when(response_queue.io.deq.bits.cmd === 3.U /*WR_CMP*/){
        mem_axi.b.valid := true.B
        mem_axi.b.bits.id := response_queue.io.deq.bits.rtnCtl(memAxiIdBits-1, 0)
        mem_axi.b.bits.resp := "b00".U //OK
        when(mem_axi.b.fire()){
          response_queue.io.deq.ready := true.B
        }
      }.elsewhen(response_queue.io.deq.bits.cmd === 7.U /*RD64_DATA*/){
        mem_axi.r.valid := true.B
        mem_axi.r.bits.id := response_queue.io.deq.bits.rtnCtl(memAxiIdBits-1, 0)
        mem_axi.r.bits.last := response_queue.io.deq.bits.scmd === 7.U
        mem_axi.r.bits.resp := "b00".U //OK
        mem_axi.r.bits.data := response_queue.io.deq.bits.readData
        when(mem_axi.r.fire()){
          response_queue.io.deq.ready := true.B
        }
      }.otherwise{
        mem_axi_error_reg := eQUEUE_CMD
      }
    }
  }

  renameSignals()
}

class RocketWolverineConfig extends Config( // rocket should be able to run at ~80MHz in this config - needs to also be changed in clocking.vh
  new With1GbRam ++
    new RocketZynqConfig)

//object GenerateVerilog extends App {
//  chisel3.Driver.execute(args, () => new WolverineTop())
//}