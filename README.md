# Watt

English|[中文](https://github.com/tuyafeng/Watt/blob/master/README_zh_CN.md)

Watt is an open-source android component management application. It uses `pm` command to disable or enable components, **requires root permission**. (It is not recommended that you root your phone unless you know what you are doing.)

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/com.tuyafeng.watt/)

### Features

1. Disable or enable components(receivers/services/activities) easily
2. Block a broadcast so that no app can receive it to wake itself
3. Block the bad Keep-Alive services of some apps without errors
4. No data collection
5. Efficient and simple interface

### Why not IFW?

In version 2.0, Watt has completely abandoned disabling components through the Intent Firewall, because ifw will cause android10+ devices to keep restarting. In view of this poor performance, Watt 2.0 uses pm commands to refactor the entire application.

### Thanks

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

