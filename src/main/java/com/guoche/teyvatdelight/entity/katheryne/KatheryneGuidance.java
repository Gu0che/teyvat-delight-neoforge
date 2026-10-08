package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.JsonObject;

/** Field guidance is separate from default gameplay choices. */
final class KatheryneGuidance {
  private KatheryneGuidance() {}

  static JsonObject forObject(JsonObject value) {
    JsonObject help = new JsonObject();
    boolean offer = value.has("sell") || value.has("cost");
    boolean target = value.has("source") && value.has("type") && !offer;
    add(help, "enabled", "是否启用：true启用，false停用。/ Enable or disable this entry.");
    add(help, "id", "这条交易的编号，自己起一个不重复的英文名字，如 apple_bundle；不是物品ID或标题。改标题不要改编号，否则限购会视为另一条交易。/ Unique trade identifier within this shop, not an item ID or title.");
    add(help, "title", "界面标题，直接填文字，如\"旅行用品\"；也可填语言键。商店留空显示文件资源ID，委托留空显示目标摘要。/ Display title: text or translation key; empty shop title uses its resource ID; commission title uses objectives.");
    add(help, "name", "这条交易的名称，如\"水果礼包\"；可填语言键，空字符串使用商品名。/ Trade name or translation key; empty uses the product name.");
    add(help, "description", "界面描述，如\"请带来3个苹果。\"；可填语言键，空字符串显示目标摘要。/ Description or translation key; empty describes the objectives.");
    add(help, "icon", "图标物品ID，如\"minecraft:apple\"；空字符串自动选择，不填图片文件名。/ Icon item ID; empty selects automatically.");
    add(help, "order", "显示顺序：数值越小越靠前；相同数值按文件名排序。/ Lower numbers appear first; ties use the resource name.");
    add(help, "weight", value.has("targets")
        ? "这道委托被抽中的相对权重；2相比1约有两倍机会，必须大于0。/ Relative commission selection weight."
        : "这个候选被抽中的相对权重；2相比1约有两倍机会，必须大于0。/ Relative candidate selection weight.");
    add(help, "type", offer ? "fixed=出售固定商品；random=出售随机抽取商品。/ fixed products or random products."
        : target ? "item=提交物品；kill=击杀生物；stat=原版统计；event=其他模组上报事件。/ Item delivery, kills, statistics or integration events."
        : "item=物品候选清单；entity=生物候选清单。/ Item or entity candidate list.");
    add(help, "source", "抽取来源：可填物品/生物ID或#标签；也可写{\"include\":[\"minecraft:apple\"],\"exclude\":[]}，或{\"pool\":\"example:supplies\"}引用公共池。统计/事件只填写具体ID。/ ID, #tag, include/exclude object, or public pool reference; stats/events use an ID.");
    add(help, "include", "允许抽到的候选：直接写ID或#标签时权重为1；也可写[\"minecraft:bread\",2]。2相比1约有两倍被抽中的机会，必须大于0。/ IDs or #tags have weight 1; [\"ID or #tag\", weight] assigns a relative weight.");
    add(help, "exclude", "从候选里排除的ID或#标签；例：[\"minecraft:bread\"]。[]表示不排除。/ IDs or tags excluded from candidates; [] excludes nothing.");
    add(help, "pool", "引用公共池文件：\"example:supplies\"对应 pools/supplies.json；不写文件后缀，不加$。引用池时不要同时填include/exclude。/ Public pool resource ID, without .json or $; do not mix with include/exclude.");
    add(help, "drawCount", "抽取几次，默认1。例：2表示抽两次；不能重复时至少需要两个不同候选。/ Number of draws, default 1; unique draws require enough candidates.");
    add(help, "count", target ? "每次抽中的目标要多少个；例：抽两次、每次3个物品，须交两份各3个。击杀5表示杀5只。/ Required amount per drawn objective."
        : "物品数量，按实际个数填写；例：3表示3个，不是抽取次数。/ Item quantity, not number of draws.");
    add(help, "repeat", "抽多个目标/商品时是否允许相同候选：false不允许，true允许。重复的物品需求或产出相加；与委托能否重复完成无关。/ Allow repeated candidates; repeated item amounts add up, unrelated to repeatable commissions.");
    add(help, "repeatable", "能否重复完成委托：true可以，false每位玩家只能成功完成一次。没完成、过期或被挤掉仍可再次接到。/ false allows only one successful completion per player; unfinished quests can return.");
    add(help, "important", "true时未完成委托不过期、不被刷新移除、置顶并用红色粗体标题；领取奖励后正常清理。/ Protect unfinished commissions, show them first in bold red; normal cleanup after settlement.");
    add(help, "targets", "全部目标一起满足才可结算；多种材料就写多个目标。例：[{\"type\":\"item\",\"source\":\"minecraft:apple\",\"count\":3}]。/ All objectives required; add entries for multiple requirements.");
    add(help, "reward", "可填奖励文件ID，如\"teyvatdelight:commission\"；也可写{\"items\":[{\"item\":\"teyvatdelight:primogem\",\"count\":3}],\"random\":[]}。{}无奖励；委托内填\"\"继承全局奖励。/ Reward resource ID or inline object; {} gives nothing; empty commission reward inherits global.");
    add(help, "items", "固定奖励列表，如[{\"item\":\"minecraft:apple\",\"count\":3}]；多项一起发，[]不给固定奖励。仅支持物品ID与数量。/ Fixed reward items and quantities; [] disables fixed rewards; custom NBT/components are not supported.");
    add(help, "random", "随机奖励列表，如[{\"type\":\"item\",\"source\":{\"pool\":\"example:supplies\"},\"drawCount\":2,\"count\":3}]；[]不抽取。/ Random item rewards; [] disables random rewards.");
    add(help, "sell", "每次交易给出的全部物品，如[{\"item\":\"minecraft:apple\",\"count\":3},{\"item\":\"minecraft:bread\",\"count\":2}]。/ All fixed products delivered per trade; item IDs and quantities only.");
    add(help, "cost", "每次交易同时收取的全部物品，如[{\"item\":\"teyvatdelight:mora\",\"count\":3}]。[]免费，材料须一次付齐。/ All payments required per trade; [] is free.");
    add(help, "item", "注册物品ID，如\"minecraft:apple\"或\"teyvatdelight:mora\"，不是中文名称。/ Registered item ID, not a translated name.");
    add(help, "dailyLimit", "每位玩家每个刷新周期可交易几次：-1不限量，0停售，正数限购。不是商品数量。/ Trades per player per refresh: -1 unlimited, 0 unavailable, positive limited.");
    add(help, "enchantEquipment", "是否给随机抽中的装备附魔；true开启，false关闭。附魔概率在全局equipment中设置。/ Enchant random equipment; probabilities are in global equipment settings.");
    add(help, "offers", "本商店的交易列表，按填写顺序显示；每个对象写编号、出售内容、价格和限购，无需单独创建商品文件。/ Ordered trades written directly in this shop; no separate product file required.");
    add(help, "refreshTime", value.has("offers")
        ? "商店刷新时刻：-1继承全局，-2不刷新；0..23999为游戏日时刻，22000=04:00，0=06:00。/ Shop refresh: -1 inherit, -2 never, or a day tick."
        : "委托刷新时刻：22000=04:00；0为结算后立即补发一道、未完成不自动过期。/ Commission refresh: 22000=04:00; 0 replaces after settlement.");
    add(help, "completionAdvancements", "完成委托后解锁的进度ID列表，如[\"minecraft:story/root\"]；[]不解锁。需已有对应进度定义。/ Advancement IDs unlocked after successful settlement; [] disables.");
    add(help, "commissions", "委托与累计奖励的全局设置；委托内容在commissions文件夹中修改。/ Global commission settings; definitions are in the commissions directory.");
    add(help, "durationTicks", "委托有效期：-1跟随刷新，0不过期，正数为接取后的游戏刻数，20刻约1秒。只影响新委托，停机不计时。/ -1 follows refresh; 0 never expires; positive game ticks after assignment; new commissions only.");
    add(help, "dailyCount", "每次刷新新增几道普通委托，1..limit；重要和紧急委托另行派发，不占此数量。刷新时间为0时每领取一道只补一道。/ Ordinary commissions per refresh, 1..limit; important and urgent assignments are additional; time 0 replaces one after claiming.");
    add(help, "limit", "进行中的普通委托上限，1..64，默认4；重要、已经达标待领奖和已经领奖的记录不占上限。/ Active ordinary cap; important, reward-ready and claimed records are exempt.");
    add(help, "balanceItems", "界面右上角显示余额的物品，按顺序填写0到2个不同物品ID。例：[\"minecraft:emerald\",\"minecraft:diamond\"]；[]隐藏余额。省略时显示摩拉和原石。修改后执行 /teyvatdelight katheryne reload。/ Header balance items: 0..2 distinct item IDs; [] hides balances; omitted defaults to Mora and Primogems. Reload with /teyvatdelight katheryne reload.");
    add(help, "selection", "random按委托权重随机；rotation按顺序轮换委托及候选目标。/ random selects by commission weight; rotation cycles commissions and candidates.");
    add(help, "milestone", "累计完成委托的额外奖励。当前轮门槛和奖励保留，新设置下一轮生效。/ Settlement milestone; changes apply to the next cycle.");
    add(help, "every", "每成功结算几道可领取一份累计奖励，1..1000000；只取得目标进展不计数。/ Successful commission settlements required per bonus, 1..1000000.");
    add(help, "shopRefreshTime", "全局商店刷新时刻：-1跟随委托；0..23999为游戏日时刻，22000=04:00，0=06:00。各商店可单独覆盖。/ Global shop refresh: -1 follows commissions or a day tick; individual shops can override.");
    add(help, "equipment", "随机装备的附魔设置，保持适用性、相容性和合法等级限制，包含宝藏附魔。/ Random equipment enchantments respect applicability, compatibility and legal levels; treasures included.");
    add(help, "firstChance", "第一个附魔概率，0..1；0.7表示70%，失败立即停止，可能没有附魔。/ First enchantment probability; 0.7 means 70%; failure stops.");
    add(help, "chanceStep", "每次成功后下次概率减少多少，0..1；失败或概率不大于0停止。0表示不递减。/ Chance reduction per enchantment; stop on failure or nonpositive chance; 0 disables reduction.");
    add(help, "allowCurses", "是否允许随机出现诅咒：true允许，false排除。/ Include curses in random enchantments.");
    return help;
  }

  private static void add(JsonObject help, String field, String text) {
    help.addProperty(field, text);
  }
}
