require_relative '../lib/tp'

# ------------------------------------------------------------ Tests de Before and After ------------------------------------------------------------
class PrimerContrato

  include Contract

  before_and_after_each_call(proc{puts "Soy el before"}, proc{puts "Soy el after"})

  def enElMedio
    puts "Estoy InBetween"
  end

  def suma(num1, num2)
    resultado = num1+num2

    puts "#{num1} + #{num2} = #{resultado}"
  end
end

# ------------------------------------------------------------ Tests de Invariantes ------------------------------------------------------------
class Guerrero

  include Contract

  attr_accessor :vida, :fuerza

  def initialize(nueva_vida, nueva_fuerza)
    @vida = nueva_vida
    @fuerza = nueva_fuerza
  end

  invariant { vida > 0 }
  invariant { fuerza > 0 && fuerza < 100 }

  def atacar(otro)
    otro.vida -= fuerza

    puts "el atacado quedó con #{otro.vida} de vida"
  end

end

# ------------------------------------------------------------ Tests de Pre y Post Condiciones ------------------------------------------------------------

# -------------------------------- PARTE 1 - DIVISION --------------------------------
class Operaciones

  include Contract

  pre { divisor != 0 } # pre-condición de dividir

  post { |result| result * divisor == dividendo } # post-condición de dividir

  def dividir(dividendo, divisor)
    dividendo / divisor
  end

  # este método no se ve afectado por ninguna pre/post condición
  def restar(minuendo, sustraendo)
    minuendo - sustraendo
  end

end

# -------------------------------- PARTE 2 - VALORES --------------------------------

class Valor1

  include Contract

  attr_accessor :valor

  def initialize(valor)
    @valor = valor
  end

  pre {
    valor_maximo = 5
    valor < valor_maximo
  }

  post {
    valor_minimo = 1
    valor > valor_minimo
  }

  def mostrar
    puts valor
  end

end

class Valor2

  include Contract

  attr_accessor :valor

  def initialize(valor)
    @valor = valor
  end

  pre {
    valor < valor_maximo
  }

  def mostrar
    puts valor
  end

  def valor_maximo
    5
  end

end

valor4 = Valor2.new(3)
valor4.mostrar

# ------------------------------------------------------------ El test de la Pila ------------------------------------------------------------
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

  pre { !empty?}
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