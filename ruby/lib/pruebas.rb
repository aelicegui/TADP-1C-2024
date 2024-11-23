require_relative 'tp'

class Ejemplo
  include Contract

  before_and_after_each_call(proc { puts "HOLA"}, proc { puts "CHAU"})

  def mensaje_1
    puts "mensaje_1"
  end

  def mensaje_2
    puts "mensaje_2"
  end
end

class Ejemplo2
  include Contract
  def mensaje_1
    puts "mensaje_1"
  end

  def mensaje_2
    puts "mensaje_2"
  end
end

class Ejemplo3
  include Contract

  attr_accessor :nombre

  def initialize(nombre)
    @nombre = nombre
  end

  before_and_after_each_call(proc { puts "HOLA"},proc { puts "CHAU"})
  before_and_after_each_call(proc { puts "HOLA #{nombre}" },proc { puts "CHAU #{nombre}"})

  def mensaje_1
    puts "mensaje___1"
  end

  def mensaje_2
    puts "mensaje___2"

  end
end

clase1 = Ejemplo.new
clase2 = Ejemplo2.new
clase3 = Ejemplo3.new("Jorge")



Ejemplo.define_method(:mensaje_3)do
  puts "mensaje_3"
end


puts "Objeto con before y after"
clase1.mensaje_1

puts "Objeto sin before y after"
clase2.mensaje_1

puts "Objeto con el metodo agregado en runtime"
clase1.mensaje_3

puts "Objeto con 2 before y 2 after"
clase3.mensaje_1

class Guerrero
  include Contract

  attr_accessor :vida, :fuerza

  def initialize(nueva_vida, nueva_fuerza)
    @vida = nueva_vida
    @fuerza = nueva_fuerza
  end

  invariant { vida >= 0 }

  invariant { fuerza > 0 && fuerza < 100 }

  def atacar(otro)
    otro.vida -= fuerza
    puts "el atacado quedó con #{otro.vida} de vida"
  end

end
#atacante4 = Guerrero.new(-1, 11)
atacante = Guerrero.new(100, 11)
atacante2 = Guerrero.new(100, 40)
atacado = Guerrero.new(50, 40)

atacante2.atacar(atacado)

#atacante.atacar(atacado)

puts atacado.vida

class Valor1

  include Contract

  attr_accessor :valor

  def initialize(valor)
    @valor = valor
  end

  pre {
    tamanio_maximo = 5
    valor < tamanio_maximo
  }

  post {
    tamanio_minimo = 1
    valor > tamanio_minimo
  }

  def mostrar
    puts valor
  end

end

valor1 = Valor1.new(2)
valor1.mostrar
#valor2 = Valor1.new(0)
#Valor2.mostrar
valor3 = Valor1.new(10)
valor3.mostrar

class Valor2

  include Contract

  attr_accessor :valor

  def initialize(valor)
    @valor = valor
  end

  pre {
    tamanio_maximo = 5
    valor < tamanio_maximo
  }

  # post { puts mayor_a_uno(valor) }

  def mostrar
    puts valor
  end

end

valor4 = Valor2.new(3)
valor4.mostrar
valor5 = Valor2.new(7)
valor5.mostrar

class Pila
  include Contract

  attr_accessor :current_node, :capacity

  invariant { capacity >= 0 }

  post { empty? }
  def initialize(capacity)
    @capacity = capacity
    @current_node = nil
  end

  pre { !full? }
  post { height > 0 }
  def push(element)
    @current_node = Node.new(element, current_node)
  end

  pre { !empty? }
  def pop
    element = top
    @current_node = @current_node.next_node
    element
  end

  pre { !empty? }
  def top
    current_node.element
  end

  def height
    empty? ? 0 : current_node.size
  end

  def empty?
    current_node.nil?
  end

  def full?
    height == capacity
  end

  Node = Struct.new(:element, :next_node) do
    def size
      next_node.nil? ? 1 : 1 + next_node.size
    end
  end
end


pila = Pila.new(1)
 puts pila.height
 puts "paso 1"
 pila.push(1)
 puts pila.height
 puts "paso 2"
#pila.push(2)
pila.pop
puts pila.height
 puts "paso 3"
pila.pop
puts pila.height
  puts "paso 4"

class Operaciones
  include Contract
  #precondición de dividir
  pre { divisor != 0 }
  #postcondición de dividir
  post { |result| result * divisor == dividendo }
  def dividir(dividendo, divisor)
    dividendo / divisor
  end

  # este método no se ve afectado por ninguna pre/post condición
  def restar(minuendo, sustraendo)
    minuendo - sustraendo
  end

end

operaciones = Operaciones.new
#puts operaciones.restar(5,2)
#puts operaciones.restar(5,0)
#puts operaciones.dividir(4,2)
#puts operaciones.dividir(4,0)


