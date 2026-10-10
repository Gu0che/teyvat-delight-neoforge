# 本地变动报告：寻仙的美食家与基岩模型自动加载

建议更新描述标题：**新增寻仙的美食家烹饪方块，支持静态基岩模型自动扫描加载**。

本轮修复描述标题：**修复寻仙的美食家地面遮挡与锅壁内外贴图错配**。

本报告记录 2026-10-10 测试分支 `test/runtime-bedrock-adepti-stove` 的变更范围。基于 `main` 的 `ad9450b`，模组版本保持 `1.1.10`，本次为测试更新，不合并主分支、不创建标签或正式 Release。

## 新方块的使用方式

- 名称：寻仙的美食家；方块和物品 ID：`teyvatdelight:adepti_seekers_stove`。
- 提瓦特乐事创造栏中获取，或者使用 `/give @s teyvatdelight:adepti_seekers_stove`。
- 按用户选择不添加合成配方。没有新增食谱，直接使用农夫乐事现有的 cooking 配方。
- 继承农夫乐事锅的烹饪、容器取餐、库存、经验、水浸和比较器逻辑；仍需下方有效热源，不提供内置无限热源。
- 使用自己的方块实体和菜单类型，避免存档变成原版农夫乐事锅、界面因方块 ID 不匹配立即关闭。
- 注册侧向物品能力，沿用农夫乐事输入输出规则；破坏掉落保留成品食物和容器组件，其他库存按农夫乐事规则散落。
- 物品栏和手持显示三维模型，不使用平面物品图标。PNG 是模型 UV 贴图。
- 碰撞和选中箱为中央 `16 × 15.5 × 16` 像素包围箱，不逐个模拟装饰立方体。
- 保留农夫乐事的朝向和 support 状态行为；静态模型不附加原锅的提手/托盘外观切换。

## 本次修改的已有文件

以下路径均相对于仓库根目录。

| 文件 | 改动 |
| --- | --- |
| `src/main/java/com/guoche/teyvatdelight/registry/ModBlocks.java` | 注册新烹饪方块 |
| `src/main/java/com/guoche/teyvatdelight/registry/ModItems.java` | 注册对应可携带食物的方块物品 |
| `src/main/java/com/guoche/teyvatdelight/registry/ModBlockEntityTypes.java` | 注册新方块实体类型 |
| `src/main/java/com/guoche/teyvatdelight/registry/ModMenuTypes.java` | 注册复用农夫乐事槽位逻辑的新菜单 |
| `src/main/java/com/guoche/teyvatdelight/registry/ModCreativeTabs.java` | 在创造栏追加新物品 |
| `src/main/java/com/guoche/teyvatdelight/client/TeyvatDelightClient.java` | 为新菜单绑定农夫乐事锅界面；既有凯瑟琳注册保留 |
| `src/main/resources/assets/teyvatdelight/lang/zh_cn.json` | 追加中文名称 |
| `src/main/resources/assets/teyvatdelight/lang/en_us.json` | 追加英文名称 |
| `src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json` | 追加新方块的镐挖掘标签 |

## 本次新增的实现与资源

| 文件 | 用途 |
| --- | --- |
| `src/main/java/com/guoche/teyvatdelight/block/AdeptiSeekersStoveBlock.java` | 方块行为、碰撞、tick、取方块 |
| `src/main/java/com/guoche/teyvatdelight/block/AdeptiSeekersStoveBlockEntity.java` | 继承烹饪逻辑，修正类型、物品和界面名称 |
| `src/main/java/com/guoche/teyvatdelight/inventory/AdeptiSeekersStoveMenu.java` | 复用槽位、配方书和移物逻辑，修正界面有效性检查 |
| `src/main/java/com/guoche/teyvatdelight/item/AdeptiSeekersStoveItem.java` | 携带食物时的份数条和食物提示 |
| `src/main/java/com/guoche/teyvatdelight/integration/AdeptiSeekersStoveCapabilities.java` | 漏斗等自动化输入输出 |
| `src/main/resources/assets/teyvatdelight/bedrock/blocks/adepti_seekers_stove.geo.json` | 导入用户提供的 61 立方体模型 |
| `src/main/resources/assets/teyvatdelight/textures/bedrock/blocks/adepti_seekers_stove.png` | 导入同名 64×64 UV PNG |
| `src/main/resources/assets/teyvatdelight/blockstates/adepti_seekers_stove.json` | 各状态对应模型入口 |
| `src/main/resources/assets/teyvatdelight/models/block/adepti_seekers_stove.json` | 模型入口、粒子贴图及继承的显示变换；实际几何由加载器生成 |
| `src/main/resources/assets/teyvatdelight/models/item/adepti_seekers_stove.json` | 物品使用同一个三维模型 |
| `src/main/resources/data/teyvatdelight/loot_table/blocks/adepti_seekers_stove.json` | 方块掉落，复制名称、食物和容器组件 |

导入副本只改变文件路径/文件名以匹配注册 ID；原始 `.bbmodel`、`.geo.json`、PNG 未修改。根据用户授权，加载器兼容模型中的负尺寸立方体，保留端点顺序、面方向和 UV，不强制将其改为正尺寸。

## 前一阶段的加载器变动（本地仍包含）

| 文件 | 用途 |
| --- | --- |
| `README.md` | 增加模型加载文档链接 |
| `build.gradle` | 可选英文构建输出路径、JUnit 5、NeoForge 测试环境；本次把开发测试世界放在构建目录中 |
| `BEDROCK_MODELS.md` | 自动加载约定、可选设置和限制；本次补充带符号尺寸说明 |
| `src/main/java/com/guoche/teyvatdelight/client/bedrock/BedrockGeometry.java` | 几何、层级、旋转、膨胀、UV 解析；本次增加负尺寸兼容 |
| `src/main/java/com/guoche/teyvatdelight/client/bedrock/BedrockModelSettings.java` | ID、贴图、状态选择、位移/旋转/缩放等可选配置 |
| `src/main/java/com/guoche/teyvatdelight/client/bedrock/BedrockSpriteSource.java` | 所有命名空间的资源目录递归扫描、图集收集和重载快照 |
| `src/main/java/com/guoche/teyvatdelight/client/bedrock/BedrockBakedModel.java` | 静态模型烘焙成方块/物品四边形 |
| `src/main/java/com/guoche/teyvatdelight/client/bedrock/BedrockBlockModels.java` | 按已注册方块 ID 替换模型和物品模型 |
| `src/main/resources/assets/minecraft/atlases/blocks.json` | 给方块图集追加自动扫描源，不替换原图集内容 |
| `src/test/java/com/guoche/teyvatdelight/client/bedrock/BedrockGeometryTest.java` | 几何测试；本次补充负尺寸、实际模型及 PNG 尺寸测试 |
| `src/test/java/com/guoche/teyvatdelight/client/bedrock/BedrockSpriteSourceTest.java` | 扫描、资源覆盖、错误隔离和重载测试 |
| `src/test/java/com/guoche/teyvatdelight/client/bedrock/BedrockBlockModelsTest.java` | 方块与物品绑定、状态选择、朝向和失败回退测试 |
| `examples/bedrock-block-models/pack.mcmeta` | 可选示例资源包元信息 |
| `examples/bedrock-block-models/assets/teyvatdelight/bedrock/blocks/primogem_block.geo.json` | 原石块示例几何 |
| `examples/bedrock-block-models/assets/teyvatdelight/bedrock/blocks/primogem_block.model.json` | 原石块示例设置 |
| `examples/bedrock-block-models/assets/teyvatdelight/bedrock/blocks/mora_block.geo.json` | 摩拉块示例几何 |
| `examples/bedrock-block-models/assets/teyvatdelight/bedrock/blocks/mora_block.model.json` | 摩拉块示例设置 |
| `LOCAL_CHANGES.md` | 本报告 |

示例和测试不进入发布 jar，示例资源包不会自动启用。凯瑟琳实体、摆件渲染器、Java 模型、贴图、动画和玩法没有修改；不再含先前已撤销的摆件渲染器接入。

以后替换**已经注册的普通方块**外观时，按方块 ID 放入 `assets/<modid>/bedrock/blocks/<id>.geo.json` 和 `assets/<modid>/textures/bedrock/blocks/<id>.png` 即可，不需再写专用模型加载代码；新增真正的方块与功能仍然需要注册。

## 验证结果与限制

- `jar --offline` 成功，标准构建依赖保持农夫乐事 1.3.2、NeoForge 21.1.244。
- 26 项 JUnit 测试通过，包括用户模型的 366 个面、带符号 UV 和 64×64 贴图尺寸，以及本轮新增的坐标转换、内外壁、上下 UV 和复用 UV 回归测试。
- 4 项服务端 GameTest 通过：注册与库存存档/重载、侧向自动化、菜单有效性和食物组件保留、无热源不烹饪/岩浆块热源制作米饭，以及四朝向不遮掉下方草方块顶面。
- 使用游戏端现有的农夫乐事 **1.3.3 jar** 又运行上述 4 项 GameTest，全部通过；未下载另一份农夫乐事。
- GameTest 使用 NeoForge 21.1.244 开发环境；游戏端为 21.1.248，尚未实际启动该客户端做视觉验收。
- 不支持基岩动画播放；物品模型朝向、真实游戏灯光、Sodium 兼容显示仍需游戏内验收。
- 模型入口自身没有备用实体几何；若人为删掉模型/PNG 或换入无法解析的几何，会记录错误并回退到入口模型，可能不显示几何，需要按日志修正资源。
- 未重新下载 Minecraft 游戏 assets；单元测试只读取本机现有 assets。测试服务端补齐了缺少的 Netty 构建依赖，测试世界单独存于临时构建目录。

本地专用测试文件 `src/gameTest/java/com/guoche/teyvatdelight/test/AdeptiSeekersStoveGameTests.java` 按仓库已有 `.gitignore` 规则不入版本控制，也不进入 jar。临时构建目录中的本机农夫乐事依赖替换脚本、测试参数和测试世界同样不入库。

## 本轮截图问题修复

用户确认悬空同朝向也存在问题，并进一步确认所谓“缺壁”是面仍在、外壁变成内壁的浅色花纹，而非几何面透空。

| 文件 | 本轮改动 |
| --- | --- |
| `src/main/java/com/guoche/teyvatdelight/block/AdeptiSeekersStoveBlock.java` | 在构造器中设置 `noOcclusion()`，保持原碰撞箱，但不再用包含镂空区域的包围箱遮掉地面及邻居面 |
| `src/main/java/com/guoche/teyvatdelight/client/bedrock/BedrockGeometry.java` | 按基岩导出规则转换 X 轴、枢轴和 X/Y 旋转，修正盒式 UV 东西面布局、逐面上下 UV 起点；逐面 UV 不再错误应用盒式 mirror；保留负尺寸、绕序和复用 UV |
| `src/test/java/com/guoche/teyvatdelight/client/bedrock/BedrockGeometryTest.java` | 补充复用 UV、非对称锅壁、上下 UV、骨骼/立方体旋转、实际导出锅壁的回归检查；更新原测试中的基岩→Java 坐标期待值 |
| `src/test/java/com/guoche/teyvatdelight/client/bedrock/BedrockSpriteSourceTest.java` | 高优先级资源覆盖测试改为检查转换后的完整 X 范围，避免旧坐标假设 |
| `src/gameTest/java/com/guoche/teyvatdelight/test/AdeptiSeekersStoveGameTests.java` | 新增四朝向非遮挡及草方块顶面可见性检查；仍仅供本地测试 |
| `BEDROCK_MODELS.md` | 说明基岩坐标自动转换、复用 UV 合法，以及可选整体配置使用 Java 坐标 |
| `LOCAL_CHANGES.md` | 更新本轮报告、验证与安装信息 |

原因已用 Blockbench 基岩格式导入/导出代码及用户的非对称锅壁数据交叉核对：原 Java 右壁 X 为 `13.2..14.1`，中心化后应为 `5.2..6.1`；基岩导出为 `origin.x=-6.1, size.x=0.9`，但原加载器直接使用该负 X 坐标，把东西面的内外材质放反。不是用户复制 UV 的问题。上下 UV 和旋转同样按该格式规则修正，`.model.json` 整体旋转语义不变。

修正后的离线四角渲染中，外壁花纹已一致。该工具仅检查几何、UV、透明与背面剔除，不能替代游戏/Sodium 的最终视觉验收。本轮未修改用户原始 `.bbmodel`、`.geo.json`、PNG 或导入资源的内容（模型副本仅有行尾格式差异，解析数据一致）；没有改凯瑟琳、烹饪逻辑或其他模组，也没有关闭透明、填充 UV 或用双面绘制掩盖错误。

## 安装产物

- 游戏端 jar：游戏 `mods/` 目录中的 `teyvatdelight-1.1.10-adepti-stove-preview.jar`。
- 源码侧产物：`outputs/teyvatdelight-1.1.10-adepti-stove-preview.jar`。
- 本轮在确认游戏退出后覆盖原文件名，不备份游戏端，不添加第二份提瓦特乐事 jar，不修改其他模组。
- 覆盖前按 jar 条目内容对比，仅 `AdeptiSeekersStoveBlock.class` 和 `BedrockGeometry.class` 两个运行时类发生变化；模型、PNG、凯瑟琳资源及其他运行时代码均与先前安装版相同。测试和诊断工具未混入 jar。
- 构建 jar、游戏端 jar 与源码侧产物的 SHA256 均为 `A686D8A54843A6FE61840E5F3ED8CCF8E0123061FA0831D7881F330258A85AE0`，复制校验一致。
- 更新 Java 类必须完整重启客户端；F3+T 仅能重载资源，不能替换已加载的代码。当前尚待用户重启后的实际客户端视觉验收。
- GitHub 推送范围限本报告列出的源码、模型/UV、文档、示例和获选测试源码。jar、构建输出、游戏资源缓存、日志、临时诊断工具、第三方模组及个人原始 `.bbmodel` 不纳入 Git 提交。
- 提交描述标题：`测试更新：新增基岩模型自动加载与寻仙的美食家，修复遮挡和 UV 对应`。
