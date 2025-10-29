# Instrucciones para Agregar Capturas de Pantalla

## Pasos para agregar tus capturas:

1. **Toma las capturas de pantalla** de tu aplicación desde el emulador o dispositivo físico

2. **Guarda las imágenes** en esta carpeta (`screenshots/`) con los siguientes nombres:
   - `login.png` - Pantalla de inicio de sesión
   - `home.png` - Pantalla principal/menú
   - `productos.png` - Pantalla de gestión de productos
   - `ventas.png` - Pantalla de registro de ventas
   - `compras.png` - Pantalla de registro de compras
   - `cierre.png` - Pantalla de cierre de caja

3. **Formato recomendado:**
   - Formato: PNG o JPG
   - Tamaño: Resolución de pantalla del dispositivo (se ajustará automáticamente)
   - Nombres: Exactamente como se indica arriba

## Cómo tomar capturas desde Android Studio:

### Opción 1: Desde el Emulador
- Ejecuta tu app en el emulador
- Haz clic en el botón de cámara en el panel lateral del emulador
- O usa `Ctrl + S` (Windows) / `Cmd + S` (Mac)

### Opción 2: Desde Dispositivo Físico
- Conecta tu dispositivo por USB
- Abre Android Studio → View → Tool Windows → Logcat
- Haz clic en el ícono de cámara en la barra de herramientas

### Opción 3: Desde el Dispositivo
- Presiona `Power + Volumen Abajo` simultáneamente
- Las capturas se guardan en la galería del dispositivo
- Transfiérelas a tu PC

## Personalización

Si quieres cambiar los nombres de las imágenes o agregar más capturas, edita el archivo `README.md` principal y modifica las líneas:

```markdown
<img src="screenshots/TU_IMAGEN.png" alt="Descripción" width="250"/>
```

## Notas:
- Las imágenes se mostrarán en filas de 3 capturas
- El ancho está configurado a 250px para verse bien en GitHub
- Puedes agregar más capturas siguiendo el mismo formato
