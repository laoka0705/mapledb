# 冒险岛怀旧服 · 安卓 App

原生安卓工程：WebView 内嵌离线数据工具，并通过原生桥接**联网抓取最新数据**（绕过浏览器 CORS 限制）。

## 功能
- 首次打开：使用内置离线数据快照（怪物/地图/掉落/装备/NPC/任务全量）
- 右上角「🔄 更新」：联网抓取最新列表数据并持久化保存
- 打开任意怪物详情：实时抓取该怪物最新掉落（含爆率）
- 数据与图片来自「冒险岛怀旧服小册子」mxdc.dvg.cn（仅供个人查询）
- 已适配折叠屏：折叠/展开自动切换布局（窄屏横排导航，宽屏侧边栏）

---

## 方式一：GitHub 云端自动打包（推荐，无需安装任何东西）

用 GitHub 的免费服务器帮你编译出 APK，全程不用装 Android Studio。

### 第 1 步：注册 GitHub
打开 https://github.com 注册一个免费账号（用邮箱即可）。

### 第 2 步：新建仓库
右上角 `+` → `New repository` → 起个名（如 `mapledb`）→ 保持 `Public` → `Create repository`。

### 第 3 步：上传工程
1. 在电脑上解压 `MapleClassicDB-工程.zip`。
2. 回到刚建的仓库页面，点 `uploading an existing file`。
3. 把解压后**文件夹里的所有内容**（`app`、`gradle`、`.github`、`build.gradle.kts` 等）**整体拖进**上传区。
4. 点 `Commit changes`。

> 注意：`.github` 是隐藏文件夹，拖拽时一起选中即可；Windows 资源管理器里可能看不到它，建议在解压后的文件夹里全选 `Ctrl+A` 再拖入。

### 第 4 步：等待自动打包
提交后，GitHub 会自动开始编译（约 5-10 分钟）：
1. 仓库顶部点 `Actions` 标签。
2. 看到 `Build APK` 在跑（黄色/绿色圆点），等它变绿色 ✓。
3. 点进这次运行，底部 `Artifacts` 区域点 `mapledb-debug-apk` 下载。

### 第 5 步：装到手机
把下载的 `app-debug.apk` 发到手机（微信/QQ/数据线），点开安装，首次需允许「安装未知来源应用」。

> 以后想更新数据：直接打开 App 点右上角「🔄 更新」即可，不用重新打包。

---

## 方式二：本地 Android Studio 打包

1. 安装 [Android Studio](https://developer.android.com/studio)。
2. `Open` → 选择本目录 `MapleClassicDB`。
3. 等 Gradle 同步完成（首次联网下载依赖，几分钟；提示缺 wrapper 时选默认让它自动处理）。
4. `Build → Build APK(s)`，APK 在 `app\build\outputs\apk\debug\app-debug.apk`。

---

## 华为 Mate XTs / HarmonyOS 说明
- 本 App 无任何 Google 服务依赖，可正常侧载安装。
- **HarmonyOS 4.x**（含 EMUI）：可直接安装安卓 APK，正常使用。
- **HarmonyOS NEXT（纯鸿蒙）**：不再兼容安卓 APK，需要鸿蒙原生版（可另行提供 ArkTS 版本）。

## 技术要点
- `MainActivity.kt`：WebView 外壳 + `AndroidBridge`（`fetchText` 异步抓取、`saveData/loadData` 持久化）。
- `app/src/main/assets/tool.html`：内嵌数据工具（含 App 模式：检测 `window.AndroidBridge`，联网更新走原生桥接）。
- `.github/workflows/build.yml`：GitHub 云端自动打包配置。

## 版本
AGP 8.4.2 / Gradle 8.7 / Kotlin 2.0.0 / compileSdk 34 / minSdk 26（Android 8.0+）
