# Puerto SaintsDragons 0.9.73 → Minecraft 26.2 / GeckoLib 5.5.5

Este repo ya trae hecha la parte mecánica y verificable del puerto. Lo que **no** se pudo
verificar (porque este entorno no tenía acceso a Mojang/Fabric/Maven ni conocimiento de la
API real de Minecraft 26.2) queda marcado en el código con comentarios `TODO(geckolib5-port)`.

## Lo que ya está hecho

- `gradle.properties`, `build.gradle` (raíz, `common`, `fabric`): apuntan a `minecraft_version=26.2`,
  `geckolib_version=5.5.5`, Java 25 (GeckoLib 5 exige `java >= 25`).
- El jar `geckolib-fabric-26.2-5.5.5.jar` que subiste está incluido en `common/libs/` y `fabric/libs/`,
  y los `build.gradle` lo resuelven como dependencia local (`com.geckolib:geckolib-fabric-26.2:5.5.5`
  vía repo `flatDir`) porque no pude confirmar desde aquí en qué Maven público está publicado GeckoLib 5.
- Los 113 archivos que usaban `software.bernie.geckolib.*` (GeckoLib 4) fueron actualizados a
  `com.geckolib.*` (GeckoLib 5) para las 27 clases cuyo nuevo nombre/paquete pude confirmar
  comparando contra las clases reales dentro del jar que subiste (`GeoEntity`, `GeoItem`,
  `AnimatableInstanceCache`, `AnimationController`, `RawAnimation`, `GeoRenderer`, `GeoEntityRenderer`,
  `GeckoLibUtil`, etc.)
- `fabric.mod.json`: dependencia `geckolib` actualizada a `>=5.5.5`.
- Las integraciones opcionales de EMI / JEI / Jade se desactivaron (dependencias comentadas +
  código fuente excluido de compilación) porque sus coordenadas de Maven incluyen la versión de
  Minecraft (`jei-1.20.1-...`) y casi seguro no existen versiones para `26.2` todavía — dejarlas
  activas habría hecho fallar el build antes de llegar a los errores reales de GeckoLib.
- Se agregó `.github/workflows/build.yml`: al hacer push, GitHub Actions compila `:fabric:build`
  con Java 25 y sube el jar resultante (o los logs de error) como artifact.

## Lo que falta (requiere compilar y ver los errores reales, cosa que no pude hacer aquí)

Busca `TODO(geckolib5-port)` en el repo (`grep -rn "TODO(geckolib5-port)" .`). Los puntos que
quedaron sin mapeo 1:1 confirmado:

1. **`AnimationState` (32 usos)** — GeckoLib 5 reescribió el sistema de render-state; no encontré
   una clase equivalente exacta en el jar. Revisa `com.geckolib.animation.state.*`
   (`AnimationTest`, `ControllerState`, `KeyFrameEvent`) y el nuevo método de predicate de
   `AnimationController`.
2. **`EntityModelData` (12 usos)** — probablemente los datos de modelo ahora viven en
   `com.geckolib.renderer.base.GeoRenderState` en vez de pasarse sueltos.
3. **`RenderProvider` → `GeoRenderProvider`** — renombrado, ya aplicado el rename de paquete,
   pero revisa si cambió la firma de los métodos.
4. **`Color`, `SoundKeyframeEvent`, `ParticleKeyframeEvent`, `CoreGeoBone`** — sin equivalente
   claro encontrado en el jar; quedan marcados uno por uno con el paquete nuevo más probable a revisar.
5. **Firmas de métodos**: aunque el nombre de una clase se haya mapeado 1:1 (p. ej. `GeoRenderer`,
   `GeoEntityRenderer`), GeckoLib 5 introdujo un pipeline de renderizado basado en "render state"
   (`GeoRenderState`, `RenderPassInfo`, eventos `Compile*RenderStateEvent`) que casi seguro cambió
   los parámetros de métodos como `render(...)`, `getInstanceId(...)`, etc. Eso solo se ve compilando.
6. **APIs de Minecraft 26.2 en sí** (fuera de GeckoLib): no toqué nada de eso porque no tengo
   conocimiento de esa versión (es posterior a mi entrenamiento). Cualquier error de compilación
   que no mencione `geckolib` es de la API de Minecraft y hay que resolverlo con los sources
   descompilados de Loom (`./gradlew genSources`) en tu máquina/CI, algo que aquí no pude ejecutar
   porque el acceso a `piston-meta.mojang.com`, `maven.fabricmc.net`, `repo1.maven.org`, etc.
   estaba bloqueado en este sandbox.
7. **Forge**: no se tocó — el jar de GeckoLib que subiste es solo para Fabric
   (`geckolib-fabric-26.2-5.5.5.jar`). `enabled_platform` sigue incluyendo `forge` pero apuntará
   a MC 26.2 con GeckoLib 4, lo cual seguro no compila. Si no te importa Forge, lo más simple es
   quitar `forge` de `enabled_platform` en `gradle.properties` y de `settings.gradle`.
8. **Plugins de Loom/Architectury** (`dev.architectury.loom` 1.11.458, `architectury-plugin`
   3.4.164): son las versiones que traía el repo original (para 1.20.1). Si Gradle no logra
   resolver mappings/metadata para MC 26.2, lo primero a revisar es si existen versiones más
   nuevas de esos plugins con soporte para 26.2.

## Cómo seguir

1. Sube esto a tu repo de GitHub (ya trae el workflow de Actions).
2. Al hacer push, Actions va a intentar compilar. Va a fallar — es esperable — pero cada error de
   compilación te va a decir exactamente en qué línea y por qué, cosa que yo no podía ver desde acá.
3. Ve resolviendo de arriba hacia abajo: primero los `TODO(geckolib5-port)` de este documento,
   después lo que vaya saliendo del compilador (sobre todo lo de `GeoRenderState`/render-state y
   cualquier API de Minecraft 26.2 que haya cambiado).
