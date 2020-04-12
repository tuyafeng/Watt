# Watt

[English](https://github.com/tuyafeng/Watt/blob/master/README.md)|中文

Watt 是一个安卓组件管理的开源应用，使用 IFW(Intent Firewall) 来禁用或启用组件，**需要 ROOT 权限**。(不推荐 ROOT 你的手机除非你知道你在干什么。)

### 特性

1. 轻松地启用或禁用组件(广播/服务/活动)
2. 拦截广播使其无法被应用用来唤醒自身
3. 无错误地拦截掉一些应用的恶意保活服务
4. 不收集数据
5. 高效简洁的界面

### IFW 简介

IFW(Intent Firewall) 是 Android 框架的一个组件，它允许基于 XML 文件中定义的规则来调整 Intent。IFW 是在 Android 4.4.2 (API 19) 中引入的，并且仍在最新版本 Android 11 (API 29) 中起作用。由于配置 IFW 要求能够直接写入系统文件，因此只能通过系统应用程序或 root 权限配置。

IFW 如何运作？在 Android 系统中通过 Intent 启动组件，而启动的 Intent 都会经过 IFW(Intent 防火墙)。 这意味着 IFW 有权允许或拒绝任何 Intent。当将 XML 文件写入或删除时，IFW 也能够动态更新其规则集，这使其成为可动态配置的灵活系统。

你可以在[这里](https://carteryagemann.com/pages/android-intent-firewall.html)了解更多细节。

### 致谢

- [google/dagger](https://github.com/google/dagger), *Apache License, Version 2.0*
- [jaredrummler/AndroidShell](https://github.com/jaredrummler/AndroidShell), *Apache License, Version 2.0*
- [promeG/TinyPinyin](https://github.com/promeG/TinyPinyin), *Apache License, Version 2.0*

### License

```
Copyright (C) 2020 Tu Yafeng

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

