# MangoPermission

Permission request：

blog：https://blog.csdn.net/qq_30993595/article/details/84645263

### How to get this libaray into your build:

* Step 1. Add the JitPack repository to your build file

  Add it in your root build.gradle at the end of repositories:

```Gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

* Step 2. Add the dependency

```Gradle
dependencies {
	    compile 'com.github.Mangosir:MangoPermission:v1.0.0-mango'
}
```

* Step 3. Put the code in your Activity

```Java
MPermission.Build(this)
            .resetState()
            .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .apply();
            
MPermission.Build(this)
            .resetState()
            .addDialogString(getString(R.string.title),mContent)
            .shouldAlarm(true)
            .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.GET_ACCOUNTS)
            .addListener(listener)
            .apply();
```
