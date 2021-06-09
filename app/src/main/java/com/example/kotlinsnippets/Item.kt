package com.example.kotlinsnippets

data class Prop ( var k: String, var v: Any ) {

}


data class Item (var k: String, var v: Array<Prop>)


class xDataSource (var d: Array<Item>) {

    var ds = Array(100, {i -> ""})

}

val id = Prop("id", "1000")
val name = Prop("name", "Hotel Ted")
val id2 = Prop("id", "1001")
val name2 = Prop("name", "Hotel Fred")


val p = arrayOf(Prop("id", "1000"),Prop( "name", "Hotel Ted"))


val hotel1 = Item(id.toString(), arrayOf(id, name))
val hotel2 = Item(id2.toString(), arrayOf(id2, name))

val ds = xDataSource(arrayOf(hotel1, hotel2))


