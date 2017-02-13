package CrossingDetect
import chisel3.util._
import chisel3._
import dsptools._
import chisel3.experimental.FixedPoint


class CrossingDetect(val latency: Int, val width: Int=16) extends Module {
   val io = IO(new Bundle {
     val ins   = Input(Vec(latency, SInt(width.W)))
     val load  = Input(FixedPoint(width = 16, binaryPoint = 8))
     val shift = Input(Bool())
     val out   = Output(SInt(width.W))
   })
 
}
