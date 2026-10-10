# 自动加载基岩方块模型

加载器在客户端启动及资源重载时递归扫描所有启用资源包的 `assets/<命名空间>/bedrock/blocks/` 目录。它根据文件路径查找已经注册的方块，加载静态基岩几何模型并同时替换方块和对应物品的外观。普通方块使用区块模型管线，几何体只在资源重载时解析和烘焙。

## 最简单的使用方式

例如替换 `teyvatdelight:primogem_block`：

```text
你的资源包/
├── pack.mcmeta
└── assets/teyvatdelight/
    ├── bedrock/blocks/primogem_block.geo.json
    └── textures/bedrock/blocks/primogem_block.png
```

在 Blockbench 中使用基岩实体模型格式，导出带 `minecraft:geometry` 的 `.geo.json`。这里读取的是静态骨骼和立方体几何数据，方块不需要成为实体。每增加一个模型，只需增加同名模型和贴图；不用新增 Java 模型、渲染器或模型绑定列表。方块 ID 含子目录时，模型和贴图也使用相同子目录。

Minecraft 1.21.1 的资源包元数据可使用：

```json
{
  "pack": {
    "pack_format": 34,
    "description": "提瓦特乐事基岩方块模型"
  }
}
```

启用资源包后按 **F3+T** 重载。修改、增加或删除模型文件都会在下次重载生效；移除资源包会恢复原模型。高优先级资源包的同路径文件覆盖低优先级文件，模型、贴图和可选配置都遵循游戏的资源包规则。

也可以把资源放入模组的 `src/main/resources/assets/` 中，重新构建模组后使用。修改源码资源目录本身不会直接改变已经安装的 jar。

## 可选配置

与 `primogem_block.geo.json` 同目录放置 `primogem_block.model.json`。不用配置时，默认匹配同名方块和贴图，覆盖该方块全部状态，并按 `facing` 自动调整朝向。

```json
{
  "block": "teyvatdelight:primogem_block",
  "texture": "teyvatdelight:block/primogem_block",
  "geometry": "geometry.primogem_block",
  "rotation": [0, 0, 0],
  "translation": [0, 0, 0],
  "scale": [1, 1, 1],
  "auto_rotate": true,
  "ambient_occlusion": true,
  "render_type": "cutout"
}
```

| 字段 | 用途与默认值 |
| --- | --- |
| `block` | 已注册的目标方块 ID，默认由命名空间和模型文件路径推导。 |
| `texture` | 纹理资源 ID，不带 `textures/` 和 `.png`，默认 `<命名空间>:bedrock/blocks/<路径>`；可以复用已有贴图。加载器自动把它加入方块图集。 |
| `geometry` | 文件中的 `description.identifier`。只有一个几何体时可省略；多个几何体时必须指定。 |
| `states` | 可选的状态筛选，例如 `{"age":"7"}` 或 `{"pose":"normal"}`。未指定的属性匹配任意值；属性和值必须真实存在。 |
| `rotation` | 模型整体 X/Y/Z 旋转角度，单位为度，默认 `[0,0,0]`。 |
| `translation` | 模型整体平移，单位为模型像素（16 像素 = 一个方块），默认 `[0,0,0]`。 |
| `scale` | X/Y/Z 正数缩放，默认 `[1,1,1]`。 |
| `auto_rotate` | 默认 `true`。模型面向北；自动处理水平及六方向的 `facing`。没有 `facing` 时保持默认朝向。 |
| `ambient_occlusion` | 默认 `true`，是否使用环境遮蔽。 |
| `render_type` | `solid`、`cutout` 或 `translucent`，默认 `cutout`，适用于带透明镂空的模型。 |

模型原点为方块底面中心：X/Z 的 `0` 对应方块中心，Y 的 `0` 对应底面。完整方块范围为 X/Z `-8..8`、Y `0..16`。整体变换顺序为缩放、旋转、平移，再应用方块朝向；整体旋转和缩放以模型原点为中心。

`.geo.json` 的基岩坐标由加载器自动转为 Java 坐标：反向 X 轴，转换骨骼/立方体枢轴和 X/Y 旋转，并保留正确的东西面及上下 UV 方向。无需手动镜像模型或重画 UV；多个面复用同一个 UV 区域是合法的。可选 `.model.json` 中的整体位移和旋转使用转换后的 Java 坐标，不再次反向。

一个方块可以使用多个模型文件，通过各自的 `block` 和不重叠的 `states` 绑定不同状态。文件按资源 ID 排序；若绑定重叠，后来的整个文件会跳过并记录日志，避免不确定地覆盖。只有匹配方块默认状态的文件会替换对应物品，物品保留原有 GUI、手持和落地显示变换。

## 支持范围

- 现代基岩 `minecraft:geometry` 结构；静态骨骼父子层级，骨骼及立方体的枢轴和多轴旋转。
- 立方体的浮点坐标、带符号尺寸（保留内外面方向）、膨胀、UV 镜像、盒式 UV、逐面 UV、带符号的 `uv_size` 和 90 度倍数的 `uv_rotation`；没有定义 UV 的面不会绘制。
- 默认支持普通 `MODEL` 渲染方块。现有凯瑟琳实体及摆件保留原来的 Java 模型和渲染方式，没有接入基岩模型覆盖。
- 每次重载重新扫描及烘焙。几何损坏、贴图缺失、方块 ID 不存在或状态配置错误时记录具体文件路径，保留该文件对应的原模型。其他有效文件仍然加载。

这套机制改变外观。方块注册、碰撞箱、掉落、交互和发光等行为仍由已有方块决定；资源包里的模型不会创建新的方块。普通方块原有的遮挡属性仍然生效，镂空装饰模型宜用于已有的非完整遮挡方块。

第一版不播放基岩动画、不加载 `.bbmodel` 工程、多边形网格或多材质模型。UV 必须落在 `texture_width` / `texture_height` 定义的范围内，图集不能像独立纹理一样循环取样。第三方方块若使用专用方块实体渲染器，也需要该渲染器一次接入 `BedrockBlockModels.renderOverride`，普通模型替换不能自动截获其自定义绘制代码。

## 可直接试用的资源包

仓库的 `examples/bedrock-block-models/` 是一个完整示例资源包，包含原石块和摩拉块两个静态模型，复用模组已有贴图。复制该文件夹到游戏的 `resourcepacks/`，启用后即可验证多个模型自动加载。示例不打包进模组，不会自动改变默认外观。

## 从源码验证

使用 Java 21：

```powershell
.\gradlew.bat test build
```

Windows 的 Java 参数文件可能无法正确解析含中文的构建路径，表现为测试类存在但报告 `ClassNotFoundException`。可以只把构建输出放到英文路径，源码目录不用搬动：

```powershell
.\gradlew.bat test build '-PbuildOutputDir=D:/cache/teyvat-delight-build'
```

测试覆盖骨骼父子变换、旋转、UV、膨胀、损坏数据、递归扫描、多命名空间、资源包优先级以及重载后的移除行为。开发测试和示例不进入发布 jar。
