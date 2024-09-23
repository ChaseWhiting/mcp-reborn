def process = "notepad.exe"

def output = new StringBuffer()
def error = new StringBuffer()

process = process.execute()

process.consumeProcessOutput(output, error)
process.waitFor()

println("Output:")
println(output.toString())

println("Error:")
println(error.toString())
