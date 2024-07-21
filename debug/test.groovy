d
def output = new StringBuffer()
def error = new StringBuffer()

process.consumeProcessOutput(output, error)
process.waitFor()

println("Output:")
println(output.toString())

println("Error:")
println(error.toString())
