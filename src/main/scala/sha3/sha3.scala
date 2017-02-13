package sha3

import chisel3._
import chisel3.util._

class Sha3Accel(val W: Int) extends Module {
  //constants
  val r = 2*256
  val c = 25*W - r
  val round_size_words = c/W
  val rounds = 24 //12 + 2l
  val hash_size_words = 256/W
  val bytes_per_word = W/8

  val io = IO(new Bundle {
    val message = Flipped(new DecoupledIO(Vec(round_size_words, Bits(W.W))))
    val hash    = new DecoupledIO(Vec(hash_size_words, Bits(W.W)))
  })

  val ctrl = Module(new CtrlModule(W))

  ctrl.io.message_val <> io.message.valid
  ctrl.io.message_rdy <> io.message.ready

  ctrl.io.hash_val    <> io.hash.valid
  ctrl.io.hash_rdy    <> io.hash.ready

  val dpath = Module(new DpathModule(W))

}

object Sha3AccelMain extends App {
  Driver.execute(args, () => new Sha3Accel(64))
}
