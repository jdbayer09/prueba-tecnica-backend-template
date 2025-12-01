# Respuestas - Prueba Técnica Backend Developer

## 1. Escenario de Concurrencia (Black Friday) 🏃‍♂️

### Problema
Es Black Friday y el sistema recibe 50 pedidos por segundo del iPhone 15 que solo tiene 10 unidades en stock. El resultado es un inventario negativo (-5 unidades).

### Pregunta
¿Qué mecanismo de base de datos o de Spring Boot utilizarías para asegurar que nunca se venda más stock del que existe, asumiendo múltiples instancias de la API corriendo en paralelo?

### Tu Respuesta
```
En este caso elegiria 2 opciones las cuales pueden coexistir:
-   Crearia una constraint en la base de datos el cual me valide que el stock no sea negativo, 
    esto me ayuda a que desde la base de datos tengamos una regla.
-   Lo complementaria con un bloqueo del registro para que la primera interaccion de la api lo bloquee
    y la segunda tenga que esperar, el stock no va a ser negativo por que se ejecutan en secuencia.
    Utilizaria en spring @Lock(LockModeType.PESSIMISTIC_WRITE)
```

---

## 2. Pregunta Trampa de Arquitectura 🎯

### Propuesta del Junior Developer
Configurar TODAS las relaciones JPA (`@OneToMany`, `@ManyToOne`) con `FetchType.EAGER` para:
- Traer toda la data en una sola consulta
- Evitar `LazyInitializationException`
- Mejorar el rendimiento

### Pregunta
¿Aceptarías este Pull Request? ¿Por qué sí o por qué no? ¿Qué impacto tendría con millones de registros?

### Tu Respuesta
```
Utilizar EAGER no es una buena practica en todas las consultas ya que puede llevar a tener problemas de rendimiento
y utilizar mas recursos de memoria, sacar consultas con mayor cantidad de datos puede llevar a que se vuelva un problema de rendimiento.
En este caso la solucion seria utilizar FetchType.LAZY y solo traer los datos cuando se necesiten, utilizando servicios transaccionales
consultas especificas con fetch joins.
```

---

## 3. Reflexiones Adicionales (Opcional) 💭

### Sobre el Refactoring Realizado
```
Teniendo en cuenta que la funcionalidad hacia muchas cosas a la vez, lo primero que valide fue la separacion de responsabilidades
separando cada una de las acciones adicionales en su propia clase, luego fui validando cada una de estas acciones a que pertenecia, 
ya que no todo era con respecto a la orden si no que habia funciones de los productos de la orden.
```

### Patrones de Diseño Aplicados
```
Se utilizo el patron Strategy Pattern, ya que la forma de hacer el descuento se aplica en varias estrategias intercambiables.
Dependency Injection, ya que todas las dependencias no se construyen en el método si no que las recibe desde otro lugar.
```

### Posibles Mejoras Futuras
```
Se podria separar en capas y utilizar clean architecture, donde el domain tengamos toda la logica de negocio separada 
sin necesidad de depender del framework de springboot.
```
