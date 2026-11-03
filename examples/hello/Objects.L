// examples/hello/Objects.L
// Top-level functions + object members → separate class files

obj Util {
    fn double(x: Int): Int {
        ret x + x
    }
}

fn main() {
    println("objects demo")
    // Util methods compile to Util.class static methods
}