package GolayCorrelator

import chisel3.iotesters._

class GolayCorrelatorAccelTests(c: GolayCorrelator) extends PeekPokeTester(c) {

  def ConvertHexStringToSignedInt(in: String, width: Int):Int={

    var x = Integer.parseInt(in, 16)
    val neglist = "89abcdef"
    if (neglist.contains(in(0))){
      x = x - scala.math.pow(2, width).intValue 
    }
    return x 
  }


  def ConverFileToList(fn: String, width: Int): List[Int] ={
    val source = scala.io.Source.fromFile(fn).getLines.toList
    var int_list = source.map(x=> ConvertHexStringToSignedInt(x, width))
    return int_list
  }

  // Test gain 
  val dir = "/Users/qijing.huang/ChiselProjects/sdr/src/test/scala/receiver/slow_rx_baseband/PreambleProcessing/GolayCorrelator_Test/"
  val in = ConverFileToList(dir+"In.dat", 8)

  val Ca = ConverFileToList(dir+"Ca_expected.dat", 16)
  val Cb = ConverFileToList(dir+"Cb_expected.dat", 16)

  for (i <- 0 until Ca.size){
  //for (i <- 0 until 30){
    //println(i.toString)
    poke(c.io.in, in(i))
    //println("in:" + peek(c.io.in).toString)
    //val wsub = peek(c.io.test)
    //println("wsubout:"+wsub.toString)

    // TODO Convert 65535 to 0xFFFF to -1 
    //var ca_out = peek(c.io.ca_out)
    //var cb_out = peek(c.io.cb_out)
    // If Ca is -1, convert it to 65535 
//    var Ca_test = Ca(i)
//    var Cb_test = Cb(i)
//    if (ca_out > 32767)
//      Ca_test = Ca(i) + 65536
//    if (cb_out > 32767)
//      Cb_test = Cb(i) + 65536
//    expect(c.io.ca_out, Ca_test)
//    expect(c.io.ca_out, Ca_test)
    expect(c.io.ca_out, Ca(i))
    expect(c.io.cb_out, Cb(i))

    //println("ca_out:" + peek(c.io.ca_out).toString + "  cb_out:" + peek(c.io.cb_out).toString)

    step(1)
  }

}
class GolayCorrelatorTester extends ChiselFlatSpec {
    behavior of "GolayCorrelatorModule"
    //backends foreach {backend =>
    //    it should s"do GolayCorrelator in $backend" in {
   private val backendNames = Array[String]("firrtl", "verilator")

   for ( backendName <- backendNames ) { 
            Driver(() => new GolayCorrelator(), backendName)(c => new GolayCorrelatorAccelTests(c)) should be (true)
    }
}

object GolayCorrelatorTester extends App {
    Driver.execute(args, () => new GolayCorrelator()){ c => new GolayCorrelatorAccelTests(c) }
}

class DelayUnitTests(c: Delay) extends PeekPokeTester(c) {

  for (i<-0 until 10){
    poke (c.io.in, i)
    //val out = peek(c.io.out)
    //println(out.toString)

    val expect_out = i - 3 
    if (expect_out >= 0){
      expect(c.io.out, expect_out)
    }else{
      expect(c.io.out, 0)
    }
    step(1)
  }
}

class DelayTester extends ChiselFlatSpec {
   private val backendNames = Array[String]("firrtl", "verilator")
   for ( backendName <- backendNames ) { 
     "Delay" should s"delay output for x cycle (with $backendName)" in {
       Driver(() => new Delay(3), backendName) {
         c => new DelayUnitTests(c)
       } should be (true)
     }   
   }
 }
