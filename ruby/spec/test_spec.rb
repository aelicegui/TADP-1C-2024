require 'rspec'
require_relative '../lib/tp'
require_relative '../spec/test_spec_companion'

describe 'Tests de Before and After' do

  before do
    @contrato = PrimerContrato.new
  end

  it 'Uso el before and after en "enElMedio" y funciona' do
    expect{@contrato.enElMedio}.to output("Soy el before\nEstoy InBetween\nSoy el after\n").to_stdout
  end

  it 'Uso el before and after en "suma" y funciona' do
  expect{@contrato.suma(2,4)}.to output("Soy el before\n2 + 4 = 6\nSoy el after\n").to_stdout
  end

  it 'Uso el before and after con un llamado doble y funciona' do
    PrimerContrato.before_and_after_each_call(proc{puts "Soy un segundo before"}, proc{puts"Soy un segundo after"})

    expect{@contrato.enElMedio}.to output("Soy el before\nSoy un segundo before\nEstoy InBetween\nSoy el after\nSoy un segundo after\n").to_stdout
    expect{@contrato.suma(5,7)}.to output("Soy el before\nSoy un segundo before\n5 + 7 = 12\nSoy el after\nSoy un segundo after\n").to_stdout
  end

end

describe 'Tests de Invariantes' do

  before do
    @guerrero1 = Guerrero.new(100, 50)
    @guerrero2 = Guerrero.new(100, 80)
  end

  it 'El guerrero 1 ataca al guerrero 2 correctamente' do
    @guerrero1.atacar(@guerrero2)

    expect(@guerrero2.vida).to eq(50)
  end

  it 'El guerrero 1 ataca al guerrero 2 y falla (excepción) al quedar la vida en número negativo (inválido)' do
    @guerrero1.atacar(@guerrero2)

    expect{@guerrero1.atacar(@guerrero2)}.to raise_exception(InvariantException)
  end

  it 'Creo el guerrero 1 y falla (excepción) al asignar la vida en número negativo (inválido)' do
    expect{Guerrero.new(-1,150)}.to raise_exception(InvariantException)
  end

end

describe 'Tests de Pre y Post Condiciones' do

  # -------------------------------- PARTE 1 - DIVISION --------------------------------

  before do
    @operacion = Operaciones.new
  end

  it 'Realiza la división y la resta de forma correcta' do
    expect(@operacion.dividir(10,2)).to eq(5)
    expect(@operacion.restar(5,3)).to eq(2)
  end

  it 'Divide por 0 y falla (excepción)' do
    expect{@operacion.dividir(2,0)}.to raise_exception(ConditionException)
  end

  it 'Se divide y falla al no cumplir la condicion de división entera (resultado * divisor != dividendo)' do
    expect{@operacion.dividir(10,4)}.to raise_exception(ConditionException)
  end

  # -------------------------------- PARTE 2 - VALORES --------------------------------

  it 'El 2 es menor que 5 y mayor que 1 (clase Valor1)' do
    valor1 = Valor1.new(2)
    expect{valor1.mostrar}.equal?(2)
  end

  it 'El 0 no es mayor que 1, por lo que falla (clase Valor1)' do
    valor2 = Valor1.new(0)
    expect{valor2.mostrar}.to raise_exception(ConditionException)
  end

  it 'El 10 no es menor que 5, por lo que falla (clase Valor1)' do
    valor3 = Valor1.new(10)
    expect{valor3.mostrar}.to raise_exception(ConditionException)
  end

  it 'El 3 es menor que 5 (clase Valor2)' do
    valor4 = Valor2.new(3)
    expect{valor4.mostrar}.equal?(3)
  end

  it 'El 7  no es menor que 5, por lo que falla (clase Valor2)' do
    valor5 = Valor2.new(7)
    expect{valor5.mostrar}.to raise_exception(ConditionException)
  end

end

describe 'El test de la Pila' do

  before do
    @pila = Pila.new(1)
  end

  it 'Creo la pila y le pido el tamaño de la pila para ver que la crea bien' do
    expect{@pila.height}.equal?(0)
  end

  it 'Hago un push (de 1) a la pila y funciona' do
    @pila.push(1)

    expect{@pila.height}.equal?(1)
  end

  it 'Le agrego un push (de 2) a la pila y falla al tener capacidad de 1 e intentar ingresarle más (excepción)' do
    @pila.push(1)

    expect{@pila.push(2)}.to raise_exception(ConditionException)
  end

  it 'Le hago un pop cuando esta vacia y falla (excepción)' do
    expect{@pila.pop}.to raise_exception(ConditionException)
  end

end

# TODO Test de que funcionen pre y post condiciones a la vez sobre un mismo atributo