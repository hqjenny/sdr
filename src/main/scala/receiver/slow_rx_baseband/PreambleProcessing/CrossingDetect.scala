package CrossingDetect
import chisel3.util._
import chisel3._
import dsptools._
//import chisel3.experimental.FixedPoint

class CrossDet(val trigger: Int, val pos_neg: Boolean, val width: Int=16) extends Module {  
  val io = IO(new Bundle {
      val in = Input(SInt(width.W))
      val out = Output(Bits(1.W)) 
    })
  val delay_out = Reg(Bool()) 
  val trigger_lit = trigger.S(width.W)
  val ge = io.in >= trigger_lit
  val lt = io.in < trigger_lit 

  val out = Wire(Bool(false)) 
  if (pos_neg){
    out := ge  
  }else{
    out := lt
  }
  when(reset){
    delay_out := false.B
  }.otherwise{
    if (pos_neg){
      delay_out := lt
    } else{
      delay_out := ge
    }
  }
  io.out := (out && delay_out).asBits
  //io.out := Bits(1)
}

class CrossingDetect(val width: Int=16) extends Module {
  val io = IO(new Bundle {
      val ca   = Input(SInt(width.W))
      val cb   = Input(SInt(width.W))
      val ce_out   = Output(Bits(1.W))
      val ca_pos   = Output(Bits(1.W))
      val ca_neg   = Output(Bits(1.W))
      val cb_pos   = Output(Bits(1.W))
      val cb_pos_n   = Output(Bits(1.W))
      val cb_neg   = Output(Bits(1.W))
      val cb_neg_n   = Output(Bits(1.W))
    })

  // Index by output value 1-6
  // Trigger values
  val trigger_values = Array(70, -70, 70, 70, -70, -70)
  // Positive cross det and negative cross det
  val pos_neg_values : Array[Boolean] = Array(true, false, true, false, true, false)

  // Arrays of cross det
  val CD = Seq.tabulate(6){index => Module(new CrossDet(trigger_values(index), pos_neg_values(index), width)).io} 
  //val CD = Vec.tabulate(6){index => Module(new CrossDet(70, false)).io} 
  //val CD = Seq.tabulate(6){index => Module(new CrossDet(72, false)).io} 


  for(i <- 0 until 2) {
    CD(i).in := io.ca 
  }
  io.ca_pos := CD(0).out
  io.ca_neg := CD(1).out

  for(i <- 2 until 6){
    CD(i).in := io.cb
  }
  io.cb_pos := CD(2).out
  io.cb_pos_n := CD(3).out
  io.cb_neg := CD(4).out
  io.cb_neg_n := CD(5).out
}
