# Performance Optimization Guide

This guide covers performance optimization strategies implemented in Kivara and best practices for maintaining optimal performance.

## 🎯 Performance Goals

- App startup time: < 2 seconds
- Frame rate: 60 FPS (16ms per frame)
- Memory usage: < 100MB for typical usage
- APK size: < 20MB
- Network requests: < 3 seconds response time

## 🚀 Implemented Optimizations

### 1. R8 Code Shrinking & Obfuscation

**Enabled in**: `app/build.gradle.kts`

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

**Benefits**:
- Reduces APK size by 30-40%
- Removes unused code and resources
- Obfuscates code for security
- Optimizes bytecode

### 2. Build Performance

**Gradle Configuration**:

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:+HeapDumpOnOutOfMemoryError
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true

# Kotlin compiler optimizations
kotlin.incremental=true
kotlin.incremental.js=true
kotlin.incremental.multiplatform=true
```

**KSP Instead of KAPT**:
- 2x faster annotation processing
- Less memory usage
- Better incremental builds

### 3. Memory Optimization

**LeakCanary Integration**:
```kotlin
dependencies {
    debugImplementation(libs.leakcanary)
}
```

**Memory Best Practices**:
- Use `viewLifecycleOwner` in Fragments
- Clear references in `onDestroyView()`
- Avoid static Context references
- Use WeakReference when needed

### 4. Network Optimization

**OkHttp Configuration**:
```kotlin
OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .retryOnConnectionFailure(true)
    .build()
```

**Caching Strategy**:
```kotlin
// Repository with cache-first strategy
override suspend fun getUser(userId: String): User {
    // Check cache first
    val cached = database.getUser(userId)
    if (cached != null && !cached.isExpired()) {
        return cached
    }
    
    // Fetch from network
    val fresh = api.getUser(userId)
    database.insertUser(fresh)
    return fresh
}
```

### 5. UI Performance

**ViewBinding** (faster than findViewById):
```kotlin
private var _binding: FragmentHomeBinding? = null
private val binding get() = _binding!!

override fun onCreateView(inflater: LayoutInflater, ...): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null // Prevent memory leaks
}
```

**RecyclerView Optimization**:
```kotlin
recyclerView.apply {
    // Enable recycling
    setHasFixedSize(true)
    
    // Use ListAdapter for efficient updates
    adapter = MyListAdapter()
    
    // Prefetch items
    layoutManager = LinearLayoutManager(context).apply {
        initialPrefetchItemCount = 4
    }
}
```

## 📊 Profiling Tools

### Android Studio Profiler

**CPU Profiler**:
1. Run app in profile mode
2. Open Profiler: View → Tool Windows → Profiler
3. Click **CPU** timeline
4. Perform actions in app
5. Analyze method traces

**Memory Profiler**:
1. Open Memory Profiler
2. Capture heap dump
3. Analyze allocations
4. Look for memory leaks

**Network Profiler**:
1. Open Network Profiler
2. Monitor network requests
3. Analyze response times
4. Check payload sizes

### LeakCanary

Automatically detects memory leaks in debug builds.

**Usage**:
1. Run debug build
2. Use the app
3. LeakCanary shows notification if leak detected
4. Review leak trace
5. Fix the leak

### Systrace

```bash
# Capture systrace
python systrace.py --time=10 -o trace.html sched gfx view

# Open in Chrome
chrome://tracing
```

## 🔧 Optimization Checklist

### Startup Performance
- [ ] Avoid work in Application.onCreate()
- [ ] Lazy initialize dependencies
- [ ] Use content providers carefully
- [ ] Profile cold start time
- [ ] Optimize splash screen

### Runtime Performance
- [ ] Avoid work on main thread
- [ ] Use coroutines for async work
- [ ] Optimize RecyclerView with DiffUtil
- [ ] Cache expensive calculations
- [ ] Use appropriate collection types

### Memory
- [ ] Fix all memory leaks
- [ ] Optimize bitmap loading (use Glide/Coil)
- [ ] Use appropriate data structures
- [ ] Avoid creating unnecessary objects
- [ ] Monitor memory usage

### Network
- [ ] Implement request caching
- [ ] Use efficient data formats (Protocol Buffers)
- [ ] Compress requests/responses
- [ ] Batch API calls when possible
- [ ] Handle offline scenarios

### APK Size
- [ ] Enable R8 shrinking
- [ ] Enable resource shrinking
- [ ] Use vector drawables
- [ ] Optimize image assets (WebP)
- [ ] Remove unused dependencies

### Battery
- [ ] Use WorkManager for background tasks
- [ ] Batch network requests
- [ ] Optimize wake locks
- [ ] Profile battery usage
- [ ] Follow Doze mode best practices

## 📈 Performance Patterns

### 1. Lazy Loading

```kotlin
class MyFragment : Fragment() {
    // Lazy initialization
    private val viewModel: MyViewModel by viewModels()
    
    // Lazy delegate
    private val expensiveObject by lazy {
        ExpensiveObject()
    }
}
```

### 2. Object Pooling

```kotlin
class BitmapPool {
    private val pool = ArrayDeque<Bitmap>()
    
    fun obtain(width: Int, height: Int): Bitmap {
        return pool.removeFirstOrNull() 
            ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
    
    fun recycle(bitmap: Bitmap) {
        pool.addFirst(bitmap)
    }
}
```

### 3. Pagination

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name LIMIT :limit OFFSET :offset")
    suspend fun getUsers(limit: Int, offset: Int): List<User>
}
```

### 4. Caching

```kotlin
class UserRepository @Inject constructor(
    private val api: ApiService,
    private val cache: UserCache
) {
    suspend fun getUser(id: String): User {
        return cache.get(id) ?: run {
            val user = api.getUser(id)
            cache.put(id, user)
            user
        }
    }
}
```

### 5. Background Processing

```kotlin
viewModelScope.launch(Dispatchers.IO) {
    // Heavy computation on background thread
    val result = performHeavyCalculation()
    
    withContext(Dispatchers.Main) {
        // Update UI on main thread
        updateUI(result)
    }
}
```

## 🐛 Common Performance Issues

### Issue: Slow List Scrolling

**Cause**: Heavy operations in adapter's `onBindViewHolder`

**Solution**:
```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = getItem(position)
    
    // ❌ Don't do this
    // holder.image.setImageBitmap(loadBitmap(item.url))
    
    // ✅ Do this instead
    Glide.with(holder.itemView)
        .load(item.url)
        .into(holder.image)
}
```

### Issue: App Startup Slow

**Cause**: Heavy initialization in Application.onCreate()

**Solution**:
```kotlin
@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // ✅ Initialize only critical components
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree())
        
        // ❌ Don't initialize heavy libraries here
        // Use lazy initialization or WorkManager instead
    }
}
```

### Issue: Memory Leaks

**Cause**: Holding Context references

**Solution**:
```kotlin
// ❌ Don't do this
class MyViewModel(private val context: Context) : ViewModel()

// ✅ Do this instead
class MyViewModel(
    @ApplicationContext private val appContext: Context
) : ViewModel()
```

### Issue: Janky Animations

**Cause**: Main thread blocking

**Solution**:
```kotlin
// ❌ Don't do this
button.setOnClickListener {
    val result = heavyCalculation() // Blocks UI
    updateUI(result)
}

// ✅ Do this instead
button.setOnClickListener {
    viewModel.viewModelScope.launch {
        val result = withContext(Dispatchers.Default) {
            heavyCalculation()
        }
        updateUI(result)
    }
}
```

## 📱 Build Variants Optimization

### Debug vs Release

```kotlin
buildTypes {
    debug {
        isMinifyEnabled = false
        applicationIdSuffix = ".debug"
        isDebuggable = true
    }
    
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        isDebuggable = false
        proguardFiles(...)
    }
}
```

## 🎨 Resource Optimization

### Vector Drawables

```xml
<!-- Use vector drawables instead of PNG -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path android:fillColor="@color/primary"
          android:pathData="M12,2L2,7v10c0,5.55,3.84,10.74,9,12,5.16,-1.26,9,-6.45,9,-12L20,7z"/>
</vector>
```

### WebP Images

Convert PNG/JPG to WebP for smaller file sizes with same quality.

```bash
# Convert using cwebp tool
cwebp -q 80 input.png -o output.webp
```

## 🔬 Benchmarking

### Macrobenchmark (App Startup)

```kotlin
@Test
fun startupCompilationNone() = macrobenchmark(
    compilationMode = CompilationMode.None(),
    startupMode = StartupMode.COLD
) { }
```

### Microbenchmark (Method Performance)

```kotlin
@Test
fun benchmarkCalculation() = benchmarkRule.measureRepeated {
    expensiveCalculation()
}
```

## 📚 Resources

- [Android Performance Patterns](https://developer.android.com/topic/performance)
- [Android Profiler](https://developer.android.com/studio/profile/android-profiler)
- [Memory Management](https://developer.android.com/topic/performance/memory)
- [R8 Optimization](https://developer.android.com/studio/build/shrink-code)
- [Jetpack Benchmark](https://developer.android.com/topic/performance/benchmarking/benchmarking-overview)

## 🎯 Performance Targets

| Metric | Target | Critical |
|--------|--------|----------|
| Cold start | < 2s | < 5s |
| Hot start | < 1s | < 2s |
| Frame rate | 60 FPS | 30 FPS |
| Memory | < 100MB | < 200MB |
| APK size | < 20MB | < 50MB |
| Battery drain | < 5%/hour | < 10%/hour |

---

**Remember**: "Premature optimization is the root of all evil" - Always profile before optimizing!
