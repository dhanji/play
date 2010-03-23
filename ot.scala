
object OpKind extends Enumeration { 
  type Kind = Value
  val Insert, Delete = Value
}

class Op(val kind: Object, change: String, position: Int) {
}
    
class Document(text: String) {

  def operate(ops: List[Op]) = {
    // apply to text
    for (val op <- ops) { 
     
	if (op.kind == OpKind.Insert)  println("insert")
	if (op.kind == OpKind.Delete)  println("delete")
      println(op.kind)
    }
  }

  override def toString() = text
}

val ops = List(new Op(OpKind.Insert, "uuu", 3))
new Document("Hello!").operate(ops)
