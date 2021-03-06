# Massy Framework 

## 概述
Massy Framwork提供模块化开发的开发视图，它重点关注软件开发期质量，为软件模块的组织提供有利于可扩展性、可重用性、易测试性。随着软件规模的扩大，软件迭代、更新、发布、运维面对的问题会增多，难度会增大。
- 测试成本高<br>
回归测试花费代价越来越大，软件体系没有支持模块化设计，静态jsp、css等混杂在一起，业务逻辑和页面逻辑没有隔离，Bug越改越多，每次更新需要测试的范围广，延长交付工期。
- 代码臃肿，维护成本高<br>
赶工图方便，程序代码跳过必要的业务逻辑检查环节，直接访问底层代码，甚至直接读写数据库表，发生故障时排查原因困难。开发干得越多，赶工越欢，程序员在现有架构上解决问题的信心就越弱，最终会以重建系统作为解决所有问题的终极手段，
- 重建，时间成本代价能承受住吗？<br>
系统已确定要重建，但多年积累不是短时间就能通过全部完成，更重要的是，重建是技术推动而不是业务推动，过往的需求怎么梳理，工期需要多久？还有，如何让重建系统能跟上源源不断的新需求？一不小心，新系统变成备胎，上线可望但遥遥无期。

Massy Framework是一种开发视图，它的一些特性，或许能减少以上问题的困扰：
- 模块化<br>
使用装配件(Assembly)提供一个边界，在这个边界内为程序代码提供运行所需的环境。这里环境是指，依据装配件的配置定义，由框架配给装配件自身无法创造，只能依赖外部的服务资源；当装配件所需的服务资源都得到满足后，就可以从等待进入工作状态，并向框架反哺输出本地服务资源，由框架将这些服务资源提供给需要它的其他装配件，从而建立起一个相互支撑的模块链。<br>
当装配件通过配置约定了所需依赖和可输出资源后，装配件就成为一个具备自我描述的特性。装配件内的程序代码并不用担心它所需的运行环境，因为所需的服务在程序代码准备运行前就已经就绪到位了。<br>
根据运行环境的差异，可以划分为不同类型的装配件，例如：业务逻辑装配件，完成后台业务逻辑处理、提供数据持久化能力，可以运行在J2EE或者J2SE模式下;前端Web和Wap展现交互装配件，只能运行在J2EE容器中,通过网页方式和终端用户交互，管理页面展现逻辑，并从业务逻辑装配件提供的服务获得事务和数据处理的支撑;前端服务装配件，作为和其他系统通讯的窗口，验证调用方系统身份，对请求进行路由给业务逻辑装配件处理。<br>
这样的一组前端和后端的装配件共同构成业务模块。对外提供完成的业务服务能力和水平。
- 隔离性<br>
每个装配件，要求明确的定义API和SPI，尤其时业务逻辑装配件。API是提供给其他模块使用的接口，SPI是为支持可扩展性，多样化而预留的接口。两者面向的对象和适用场景不同。<br>
API定义放在独立Maven项目中，打包后生成独立的jar文件，而SPI和具体的模块功能实现代码存放在其他项目工程中。Massy Framework建议使用自定义的类加载器加载SPI和模块功能实现代码，这样能增加其他模块直接访问本模块底层代码的难度，从而有利于程序员将重心放在API的设计上，而不是图方便的乱搭桥。<br>
虽然，理论上满足Massy Framework开发规范的装配件/模块可以做到开箱即用，但实际上，Massy Framework从来没有将开箱即用作为设计目标;反而认为，客制(定制)化的需求是软件工程的天性需要，任何模块都应该预留扩展的空间和可能。<br>
另外，为了解决Jar Hell(泛指Jar的多版本共存所产生的问题)，Massy Framework引入了JBoss Modules。由JBoss Modules来约定Jar包之间的依赖关系，允许多版本Jar共存，从而缓解Jar Hell造成的问题。`
- 测试成本<br>
装配件应该以API入口黑盒测试为主，如果代码没有被修改，基本上是无需再次进行回归测试，测试人员只需要关注新功能变动所影响的装配件即可。`
- 分而治理<br>
模块的隔离性，为您提供新旧系统共存提供了解决重构的一种思路和可行办法。您完成全可以先将旧系统中打包成为Massy Framework中的一个巨模块，对于新功能，可以按新增装配件方式进行建设，并让旧系统提供出新装配件所依赖的服务即可，这样您完全可以满足业务上源源不断的新功能；同时，您需要组织人手对旧系统进行拆分，将巨模块拆分为几个大模块，将大模块拆分为中模块、小模块。每次先划分清楚模块间关系，定义好装配件的构成和API，然后在对内部的实现进行重构。这里建议，重构采用领域驱动方式进行，因为领域驱动并不在乎数据采用何种存储模式，这种思路对于旧系统/旧数据的改造无疑有着重要的意义。<br>
这样您就就能即赢得不丢弃新功能，又获得改造旧系统所需的时间成本，它进度完全可以由您控制。`

当然，解决以上问题，并不简单的认为使用Massy Framework即可。它需要软件部门规范软件的组织模型，增加对代码审查，以符合预期的规范，只有这样，才能保证Massy Framework能起到作用，并有利于问题向好的方向转换。

## 快速入门
## 使用指南
## 样例
