package CrossingDetect
import chisel3.iotesters._

class CrossingDetectTests(c: CrossingDetect) extends PeekPokeTester(c) {
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
  val dir = "/Users/qijing.huang/ChiselProjects/sdr/src/test/scala/receiver/slow_rx_baseband/PreambleProcessing/CrossingDetect_Test/"
  val ca = ConverFileToList(dir+"ca.dat", 16)
  val cb = ConverFileToList(dir+"cb.dat", 16)

  val ca_pos_expected = ConverFileToList(dir+"ca_pos_expected.dat", 1)
  val ca_neg_expected = ConverFileToList(dir+"ca_neg_expected.dat", 1)
  val cb_pos_expected = ConverFileToList(dir+"cb_pos_expected.dat", 1)
  val cb_pos_n_expected = ConverFileToList(dir+"cb_pos_n_expected.dat", 1)
  val cb_neg_expected = ConverFileToList(dir+"cb_neg_expected.dat", 1)
  val cb_neg_n_expected = ConverFileToList(dir+"cb_neg_n_expected.dat", 1)


  for (i <- 0 until ca.size){
    //println("ca" + a(i))
    poke(c.io.ca, ca(i))
    poke(c.io.cb, cb(i))
    expect(c.io.ca_pos, ca_pos_expected(i))
    expect(c.io.ca_neg, ca_neg_expected(i))

    expect(c.io.cb_pos, cb_pos_expected(i))
    expect(c.io.cb_neg, cb_neg_expected(i))

    expect(c.io.cb_pos_n, cb_pos_n_expected(i))
    expect(c.io.cb_neg_n, cb_neg_n_expected(i))

    step(1)
  }
}

class CrossingDetectTester extends ChiselFlatSpec {
   private val backendNames = Array[String]("firrtl", "verilator")
   for ( backendName <- backendNames ) { 
     "CrossingDetect" should s"outputs(with $backendName)" in {
       Driver(() => new CrossingDetect(16), backendName) {
         c => new CrossingDetectTests(c)
       } should be (true)
     }   
   }
 }

object CrossingDetectTester extends App {
    Driver.execute(args, () => new CrossingDetect()){ c => new CrossingDetectTests(c) }
}
