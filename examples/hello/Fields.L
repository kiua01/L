// examples/hello/Fields.L

obj Counter {
    mut count = 0

    fn bump(): Int {
        count = count + 1
        ret count
    }
}

fn main() {
    println("fields demo")
}