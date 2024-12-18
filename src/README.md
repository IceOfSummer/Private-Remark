# 设计

## 持久化

当保存备注时，会获取`当前文件`相对`项目根路径`的**相对路径**.

例如以 `/opt/project` 作为项目根路径(即 IDEA 打开项目的路径)，给文件 `/opt/project/src/hello.java` 创建备注时，相对路径就是 
`src/hello.java`. 此时，会给 `/opt/project` 这个路径创建一个数据库并保存相关备注。

后面再次打开 `hello.java` 时，会用 `src/hello.java` 从本地数据库中搜索。

---

问题：

1. 父工程套子工程，单独开子工程就看不到备注了。
2. 父工程和子工程不是强制关联，例如父工程可能只是一个简单的文件夹，而子工程是多个 Git 仓库

解决方法:

将每个 sqlite 数据库文件视作一个*项目*。在使用时，可以将 *项目* 绑定到一个文件夹。对于一个文件夹，
如果它自己或父文件夹没有被绑定 *项目*，那么这些文件夹里的文件不可以添加备注，也不会尝试加载备注。



