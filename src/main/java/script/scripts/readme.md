所有的Script都应该防止在该目录下

脚本具有唯一Id标识

Script 目前是无后效性的。 Script执行只能针对一般性的逻辑
不应用于处理复杂的业务逻辑。

不能够处理复杂业务
完成之后不能够


脚本可以热更新

脚本：

可以被动态替换的运行中的代码

脚本内部implementation可以修改，但是脚本公开的interface不能修改。（脚本接口修改只能够停服，重启才能更新）


用途：

1.插入到一些基本的协议过滤网中，用以控制功能的开启或者关闭(运营)(如果想要开启或者关闭某些功能） 活动的开关，紧急功能的关闭等等

2.动态替换运行中的实例，用以实现某些功能的hotfix某些功能

脚本的生命周期。有2中脚本类型， 一种是单例脚本类型，脚本是一个Singleton。另一种是Scoped脚本，运行的时候会生成一份实例。