package sha3

import chisel3._
import chisel3.util._

object common {
  //There is sometimes an issue with this function as defined below (with current chisel version)
  //The C++ emulator does not cast numbers properly in all modules (but does work in some).
  //VCS does not have this issue.

  //def ROTL(x: UInt, y: UInt, W: UInt) = (((x) << (y)) | ((x) >> (W - (y))))

  //Alternate method is slice and concat.  The C++ emulator and VCS both work with this function defn.
  def ROTL(x: UInt, shift: Int, w: Int):UInt = Cat( x(w - 1 - shift, 0), x(w - 1, w - 1 - (shift - 1)) )
}
