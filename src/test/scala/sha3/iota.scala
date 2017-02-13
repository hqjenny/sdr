package sha3

import chisel3.iotesters._

class IotaModuleTests(c: IotaModule) extends PeekPokeTester(c) {
    val W       = 64
    val round = 0
    val state = Array.fill(5*5){BigInt(3)}
    val out_state = Array.fill(5*5){BigInt(3)}
    out_state(0) = state(0) ^ BigInt(1)
    // HACK: Bug in Chisel3 requires the for loop instead of a single poke statement
    for (i <- 0 until 25)
        poke(c.io.state_i(i), state(i))
    poke(c.io.round, round)
    step(1)
    // HACK: Bug in Chisel3 requires the for loop instead of a single poke statement
    for (j <- 0 until 25)
        expect(c.io.state_o(j), out_state(j))
}

class iotaTester extends ChiselFlatSpec {
    behavior of "IotaModule"
    backends foreach {backend =>
        it should s"do the iota function in $backend" in {
            Driver(() => new IotaModule, backend)(c => new IotaModuleTests(c)) should be (true)
        }
    }
}

object iotaTester extends App {
    Driver.execute(args, () => new IotaModule){ c => new IotaModuleTests(c) }
}
