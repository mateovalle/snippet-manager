import java.time.LocalDate

//esta clase la cree para ver como devolvia los parametros Spring, dudo que la usemos.

class Snippet(private var id:Long, private var content:String, private var lastModified: LocalDate) {
    fun getId(): Long {
        return id
    }

    fun getContent():String{
        return content
    }

    fun getLastModified():LocalDate{
        return lastModified
    }

    fun setId(id:Long){
        this.id = id
    }

    fun setContent(content:String){
        this.content = content
    }

    fun setLastModified(lastModified: LocalDate){
        this.lastModified = lastModified
    }
}