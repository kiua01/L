// examples/fabric/HelloFabric.L
// Fabric-only target for now. Bear loader comes later.

pkg com.example.hellofabric

obj HelloFabric {
  fn onInitialize() {
    println("Hello Fabric from L!")
    println("Loader target: fabric")
  }
}

fn main() {
  // JVM entry for testing without Minecraft
  println("Hello Fabric from L! (dev main)")
}