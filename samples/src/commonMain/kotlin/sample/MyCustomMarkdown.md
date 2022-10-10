<!--- TOC -->

* [Part 1](#part-1)
  * [Chapter 1](#chapter-1)
  * [Chapter 2](#chapter-2)
  * [Chapter 3](#chapter-3)
* [Details of Shape](#details-of-shape)
    * [Properties](#properties)
    * [Functions](#functions)
* [Details of Position](#details-of-position)
    * [Properties](#properties)
* [Details of Polygon](#details-of-polygon)
    * [Properties](#properties)
    * [Functions](#functions)
* [Details of Circle](#details-of-circle)
    * [Properties](#properties)
    * [Functions](#functions)
* [Details of Rectangle](#details-of-rectangle)
    * [Properties](#properties)
    * [Functions](#functions)
* [Details of Oval](#details-of-oval)
    * [Properties](#properties)
    * [Functions](#functions)
* [Details of InnerOval](#details-of-inneroval)
    * [Properties](#properties)
* [Details of Square](#details-of-square)
    * [Properties](#properties)
    * [Functions](#functions)
* [Details of Builder](#details-of-builder)
    * [Functions](#functions)
* [Part 2](#part-2)
  * [Chapter 1](#chapter-1)
  * [Chapter 2](#chapter-2)
  * [Chapter 3](#chapter-3)
  * [Chapter 4](#chapter-4)

<!--- END -->
# Hello !

## Part 1

### Chapter 1

### Chapter 2

### Chapter 3

<!--$ INSERT build/generated/ksp/metadata/commonMain/resources/sample/package.md -->
# Package sample

```mermaid
classDiagram
  class Position {
    <<data class>>
    +x Float
    +y Float
  }
  click Position href "../sample/-position/index.html"
  class Shape {
    <<interface>>
    +originPosition Position
    +computeSurface() Float
  }
  Shape ..* Position : has
  click Shape href "../sample/-shape/index.html"
  class Rectangle {
    <<interface>>
    +width Float
    +height Float
    +rotate(Float) 
  }
  Shape <|-- Rectangle : implements
  click Rectangle href "../sample/-rectangle/index.html"
  class Polygon {
    <<interface>>
    +howMuchSides() Int
  }
  Shape <|-- Polygon : implements
  click Polygon href "../sample/-polygon/index.html"
  class Square {
    <<class>>
    +sideSize Float
    +publicFun() 
  }
  Polygon <|-- Square : implements
  click Square href "../sample/-square/index.html"
  class Circle {
    <<data class>>
    +radius Float
  }
  Shape <|-- Circle : implements
  click Circle href "../sample/-circle/index.html"

```

<!--$ END -->




## Part 2

nlanl

### Chapter 1

aelknra

### Chapter 2

ateaa

### Chapter 3

aer

### Chapter 4

aer
