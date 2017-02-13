package sha3

import chisel3.iotesters._

class Sha3AccelTests(c: Sha3Accel) extends PeekPokeTester(c) {
  //This test is the same as the one in the keccak code on github
  //https://github.com/gvanas/KeccakCodePackage/blob/master/TestVectors/KeccakF-1600-IntermediateValues.txt
  val test_message = Array.fill(c.round_size_words){BigInt(0)}
  val test_hash = Array(BigInt("F1258F7940E1DDE7",16),
                        BigInt("84D5CCF933C0478A",16),
                        BigInt("D598261EA65AA9EE",16),
                        BigInt("BD1547306F80494D",16))
  var timeout = 10000 // Set a time big enough such that we can handle the case that valid is never high.

  // HACK: Bug in Chisel3 requires the for loop instead of a single poke statement
  for (i <- 0 until c.round_size_words)
      poke(c.io.message.bits(i), test_message(i))
  poke(c.io.message.valid, 1)
  poke(c.io.hash.ready, 1)
  do {
    step(1)
    poke(c.io.message.valid, 0)
    timeout = timeout - 1
  } while(peek(c.io.hash.valid) == 0 && timeout > 0)

  if (timeout == 0)
      println("FAIL - Sha3AccelTest timed out.")

  expect(c.io.hash.valid, 1)

  // HACK: Bug in Chisel3 requires the for loop instead of a single expect statement
  for (i <- 0 until 4)
      expect(c.io.hash.bits(i), test_hash(i))
}

class sha3Tester extends ChiselFlatSpec {
    behavior of "Sha3Module"
    backends foreach {backend =>
        it should s"do SHA3 in $backend" in {
            Driver(() => new Sha3Accel(64), backend)(c => new Sha3AccelTests(c)) should be (true)
        }
    }
}

object sha3Tester extends App {
    Driver.execute(args, () => new Sha3Accel(64)){ c => new Sha3AccelTests(c) }
}

// vim: set ts=4 sw=4 et:
