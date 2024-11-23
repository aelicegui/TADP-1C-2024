class InvariantException < StandardError # Creo una excepción para cuando no se cumple el invariante.
  def initialize(msg="No se cumple el invariante")
    super
  end
end

class ConditionException < StandardError # Creo una excepción para cuando no se cumple una condición.
  def initialize(msg="No se cumple la condicion")
    super
  end
end

module Contract # Creo un módulo para implementar contratos

  attr_accessor :original_object

  def initialize
    @original_object = BasicObject.singleton_class
  end

  def local_variable(variable_name, value) # Defino una variable local
    self.define_singleton_method(variable_name)do # Defino un método singleton con el nombre de la variable
      return value # Devuelvo el valor dado
    end
  end

  def self.included(includer) # Incluyo el modulo en una clase
    # extiendo sus funcionalidades y realizo algunas configuraciones.

    includer.extend(ContractCompanion) # Reconozco todos los metodos perteneciente al companion object "ContratoCompanion"
    includer.initialize_setup # Uso el método "inicializar" que está en ContratoCompanion

    def includer.method_added(method) # Overraideo el method_added, para que cada vez que se añade un nuevo método a la clase implemente lo siguiente

      unless @trace_calls_internal # Verifico que no se está rastreando ya la llamada internamente ()para evitar un bucle infinito)
        @trace_calls_internal = true # Marco que estoy rastreando la llamada internamente
        Contract.inject(self, method)
        @trace_calls_internal = false # Desmarco que estoy rastreando la llamada internamente

      end
    end

    def instance_clone(instance, method_params, *args)
      local_variables_handling = instance.clone # Objeto clonado para variables locales
      method_params.each_with_index { |parametro, i| local_variables_handling.local_variable(parametro.to_s, args[i])}
      local_variables_handling # retorno el clon
    end


  end

  def self.inject(object, method) # Inyecto el contrato en un método de la clase

    object.instance_eval do  # Evaluamos el metodo en el objeto

      # Defino los valores (como inicializarlo)
      method_object = instance_method(method) # Método original
      # instance_method(): toma un símbolo que representa el nombre de un método ("metodo") y devuelve un objeto Method que representa ese método (en este caso lo asigna en "method_object")
      method_precondition = self.precondition  # Precondición del método
      method_postcondition = self.postcondition # Postcondición del método
      method_params = method_object.parameters.map(&:last) # Parámetros del método



      define_method(method) do |*args, &block| #Redefinimos el metodo

        local_variables_handling = instance_clone(self, method_params, *args) # Uso un método para clonar la instancia

        # Ejecuto el Bloque Before antes de cada mensaje
        unless @check # Verifico que no se está realizando una verificación (que no estoy dentro de una)

          @check = true # Marco que estoy verificando

          before_actions.each { |before| self.instance_eval(&before) } # Ejecuto el before (verificaciones pre-método)

          local_variables_handling.instance_eval(&method_precondition) # Evaluo la pre-condición del método usando la clase que se encarga del manejo de las clases vcaías

          @check = false # Desmarco que estoy verificando

        end

        # Invariante recursiva
        result = method_object.bind(self).call(*args, &block) # Bindeamos para devolverlo al contexto de la clase.

        local_variables_handling = instance_clone(self, method_params, *args) # Uso un método para clonar la instancia

        # Ejecuto el Bloque After luegp de cada mensaje
        unless @check # Verifico que no se está realizando una verificación (que no estoy dentro de una)

          @check = true # Marco que estoy verificando

          after_actions.each { |after| self.instance_eval(&after)} # Ejecuto el after (verificaciones post-método)


          local_variables_handling.instance_exec(result, &method_postcondition)  # Ejecuto la post-condición del método usando el resultado y la clase que se encarga del manejo de las clases vcaías

          @check = false # Desmarco que estoy verificando

        end

        result # Devuelvo el resultado

      end

      self.precondition = proc{} # Dejo la pre-condición como un bloque vacío
      self.postcondition = proc{} # Dejo la post-condición como un bloque vacío

    end
  end

  module ContractCompanion # Módulo complementario del contrato

    attr_accessor :before_actions, :after_actions, :precondition, :postcondition

    def initialize_setup # Inicializo las acciones anteriores y posteriores, y también las pre-condiciones y post-condiciones
      self.before_actions = []
      self.after_actions = []
      self.precondition = proc{}
      self.postcondition = proc{}
    end

    def before_and_after_each_call(before, after)
      self.before_actions << before
      self.after_actions << after
    end

    def invariant(&condition) # Defino el invariante

      before_and_after_each_call(proc{}, proc{unless instance_eval(&condition)
                                                raise(InvariantException)
                                              end})

    end

    def pre(&precondition) # Método para establecer la pre-condición del contrato
      self.precondition = proc { unless instance_eval(&precondition)
                                   raise ConditionException, "Precondición no satisfecha para el método llamado"
                                 end}

    end

    def post(&postcondition) # Método para establecer la post-condición del contrato
      self.postcondition = proc {|result| unless instance_exec(result, &postcondition)
                                    raise ConditionException, "Postcondición no satisfecha para el método llamado"
                                          end}
    end

  end

  def before_actions # Devuelvo las acciones anteriores de cada llamado (getter)
    self.class.before_actions
  end

  def after_actions # Devuelvo las acciones posteriores de cada llamado (getter)
    self.class.after_actions
  end

  def precondicion # Devuelvo las pre-condiciones de cada llamado (getter)
    self.class.precondition
  end

  def postcondicion # Devuelvo las post-condiciones de cada llamado (getter)
    self.class.postcondition
  end

end
