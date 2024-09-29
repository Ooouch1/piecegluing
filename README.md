# piecegluing
Implementation of enumeration of convex polygons constructed by gluing unit angle triangles, which is presented at 8OSME
as "Efficient Enumeration of Rectangles in Origami Design" by
K. Ouchi (me), H. Komatsu, R. Uehara.

## Required environment
* Java 17
* Maven
* Eclipse (optional)

## Build
```sh
mvn package
```

## Run
For 180/8 degree unit angle and 4-gon, Run:

```sh
cd target
java -jar piecegluing-0.0.2.jar 8 4 unique
```