


class Op(change: String, position: Int) {

  object Kind extends Enumeration { 
    type Kind = Value
    val Insert, Delete = Value
 }

}
    
class Document(text: String) {

  def operate(ops: List[Op]) = {
    
  }
}
