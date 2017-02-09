package GolayCorrelator
import chisel3.util._
import chisel3._

class VecShiftRegister(val latency: Int, val width: Int=16) extends Module {
   val io = IO(new Bundle {
     val ins   = Input(Vec(latency, SInt(width.W)))
     val load  = Input(Bool())
     val shift = Input(Bool())
     val out   = Output(SInt(width.W))
   })
   val delays = Reg(Vec(latency, SInt()))
   when (io.load) {
     for( i <- 0 until latency){ 
        delays(i) := io.ins(i)
      }
   } .elsewhen(io.shift) {
     delays(0) := io.ins(0)
     for(i <- 0 until latency-1){
      delays(i+1) := delays(i)
     }
   }
   io.out := delays(latency-1)
}

class VecShiftRegisterSimple(val n: Int, val w: Int) extends Module {
   val io = IO(new Bundle {
     val in  = Input(SInt(w.W))
     val out = Output(SInt(w.W))
   })  
 
   val initValues = Seq.fill(n) { 0.S(w.W) }
   val delays = Reg(init = Vec(initValues))
 
   for (i <- n-1 to 1 by -1) {
     delays(i) := delays(i - 1)
   }
 
   delays(0) := io.in
   io.out := delays(n-1)
 }


class Delay (val latency: Int) extends Module{
  val io = IO(new Bundle{
    val in = Input(SInt(16.W))
    val out = Output(SInt(16.W))
    })
  // TODO shift registers
  //io.out := ShiftRegister(io.in, latency)  
  val SR = Module(new VecShiftRegisterSimple(latency, 16))
  SR.io.in := io.in
  io.out := SR.io.out 
}

class GolayCorrelatorBase (val mult_pipeline: Int=3, val dSub: Int, val wSub: Int) extends Module {
  val io  = IO(new Bundle {
      val ra_in  = Input(SInt(16.W))
      val rb_in = Input(SInt(16.W))
      val ra_out = Output(SInt(16.W))
      val rb_out = Output(SInt(16.W))
      val test = Output(SInt(16.W))
      })
    val ra_in_delay = Module(new Delay(mult_pipeline))

    ra_in_delay.io.in := io.ra_in  
    val gain = wSub.S(16.W)
    val wSubout = ra_in_delay.io.out * gain 
    
    val rb_in_delay = Module(new Delay(dSub + mult_pipeline)) 
    rb_in_delay.io.in := io.rb_in

    io.ra_out := wSubout + rb_in_delay.io.out 
    io.rb_out := wSubout - rb_in_delay.io.out 
    io.test := wSubout
}

// Convert from 8 bit int to 16 bit int 
class Convert(val in_width: Int=8, val out_width: Int=16) extends Module{
  val io = IO(new Bundle{
    val in = Input(SInt(in_width.W))
    val out = Output(SInt(out_width.W))
  }) 

  val in_ext = Fill(out_width-in_width, io.in(7)) 
  val out = Cat(in_ext, io.in) 
  // Cat returns UInt
  // println(out)
  io.out :=  out.asSInt
  
}

class GolayCorrelator(regular_pipeline: Int=1) extends Module{

  val io  = IO(new Bundle {
      val in  = Input(SInt(8.W))
      val ca_out = Output(SInt(16.W))
      val cb_out = Output(SInt(16.W))
      val test = Output(SInt(16.W))
      })
  val D_128 = Array(1, 8, 2, 4, 16, 32, 64)
  val W_128 = Array(-1, -1, -1, -1, 1, -1, -1)

  // TODO GolayCorrelatorBase
  //val rows = Vec.tabulate(R){index => Module(new ArrayRowModule(W=W, V=V, H=H, G=G, I=index)).io}
  val GCB = Vec.tabulate(7){index => Module(new GolayCorrelatorBase(3, D_128(index), W_128(index))).io} 
  val delay_a = Vec.fill(7){Module(new Delay(regular_pipeline)).io} 
  val delay_b = Vec.fill(7){Module(new Delay(regular_pipeline)).io} 
  // TODO convert input signal 

  val convert = Module(new Convert()).io
  convert.in := io.in
  GCB(0).ra_in := convert.out
  GCB(0).rb_in := convert.out
  for (i <- 0 until 6){
    delay_a(i).in := GCB(i).ra_out  
    delay_b(i).in := GCB(i).rb_out  
    GCB(i+1).ra_in:= delay_a(i).out
    GCB(i+1).rb_in:= delay_b(i).out
  }

  io.test := GCB(0).test
  
  delay_a(6).in := GCB(6).ra_out
  delay_b(6).in := GCB(6).rb_out
  
  io.ca_out := delay_a(6).out
  io.cb_out := delay_b(6).out

}

