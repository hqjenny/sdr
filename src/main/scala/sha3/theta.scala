package sha3

import chisel3._

class ThetaModule(val W: Int = 64) extends Module {
  val io = IO(new Bundle {
    val state_i = Input (Vec(5*5, Bits(W.W)))
    val state_o = Output(Vec(5*5, Bits(W.W)))
  })
  //YOUR IMPLEMENTATION HERE
}
