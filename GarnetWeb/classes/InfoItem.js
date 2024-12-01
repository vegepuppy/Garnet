class InfoItem {
  constructor(display, content, id, belong) {
    this.belong = belong;
    this.id = id;
    this.content = content;
    this.display = display;
  }
}

module.exports = InfoItem;
