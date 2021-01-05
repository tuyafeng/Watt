# Watt

[English](https://github.com/tuyafeng/Watt/blob/master/README.md)|中文

Watt 是一个安卓组件管理的开源应用，使用 `pm` 命令来禁用或启用组件，**需要 ROOT 权限**。(不推荐 ROOT 你的手机除非你知道你在干什么。)

### 特性

1. 轻松地启用或禁用组件(广播/服务/活动)
2. 拦截广播使其无法被应用用来唤醒自身
3. 无错误地拦截掉一些应用的恶意保活服务
4. 不收集数据
5. 高效简洁的界面

### 为什么弃用 IFW？

在 2.0 版本，Watt 完全抛弃了 IFW（Intent Firewall）的实现，改用传统的 pm 命令来禁用组件。因为在 android10+ 设备上，写入 IFW 规则会造成设备不断重启，鉴于这个糟糕表现，Watt 2.0 使用了 pm 命令重构了整个应用。

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

