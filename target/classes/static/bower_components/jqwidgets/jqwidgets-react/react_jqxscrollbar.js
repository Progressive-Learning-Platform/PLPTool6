/*
jQWidgets v4.5.0 (2017-Jan)
Copyright (c) 2011-2017 jQWidgets.
License: http://jqwidgets.com/license/
*/
import React from 'react';

let jqxScrollBar = React.createClass ({
  getInitialState: function () {
    return { value: '' };
  },
  componentDidMount: function () {
      let options = this.manageAttributes();
    this.createComponent(options);
  },
  manageAttributes: function () {
      let properties = ['disabled','height','largestep','min','max','rtl','step','showButtons','thumbMinSize','theme','vertical','value','width'];
      let options = {};
    for(let item in this.props) {
        if(item === 'settings') {
          for(let itemTwo in this.props[item]) {
            options[itemTwo] = this.props[item][itemTwo];
              }
          } else {
              if(properties.indexOf(item) !== -1) {
              options[item] = this.props[item];
              }
          }
      }
      return options;
    },
  createComponent : function (options) {
    if(!this.style) {
        for (let style in this.props.style) {
          $('#' +this.componentSelector).css(style, this.props.style[style]);
        }
    }
    if(this.props.className !== undefined) {
      let classes = this.props.className.split(' ');
      for (let i = 0; i < classes.length; i++ ) {
        $('#' +this.componentSelector).addClass(classes[i]);
      }
    }
    if(!this.template) {
        $('#' +this.componentSelector).html(this.props.template);
    }
    $('#' +this.componentSelector).jqxScrollBar(options);
  },
  generateID : function () {    
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
  },
  setOptions: function (options) {
    $('#' +this.componentSelector).jqxScrollBar('setOptions', options);
  },
  getOptions: function () {
    if(arguments.length === 0) {
      throw Error('At least one argument expected in getOptions()!');
    }
    let resultToReturn = {};
    for(let i = 0; i < arguments.length; i++) {
      resultToReturn[arguments[i]] = $('#' +this.componentSelector).jqxScrollBar(arguments[i]);
    }
    return resultToReturn;
  },
  on: function (name,callbackFn) {
    $('#' +this.componentSelector).on(name,callbackFn);
  },
  off: function (name) {
    $('#' +this.componentSelector).off(name);
  },
  disabled: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("disabled", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("disabled");
    }
  },
  height: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("height", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("height");
    }
  },
  largestep: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("largestep", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("largestep");
    }
  },
  min: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("min", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("min");
    }
  },
  max: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("max", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("max");
    }
  },
  rtl: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("rtl", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("rtl");
    }
  },
  step: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("step", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("step");
    }
  },
  showButtons: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("showButtons", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("showButtons");
    }
  },
  thumbMinSize: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("thumbMinSize", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("thumbMinSize");
    }
  },
  theme: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("theme", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("theme");
    }
  },
  vertical: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("vertical", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("vertical");
    }
  },
  value: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("value", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("value");
    }
  },
  width: function (arg) {
    if (arg !== undefined) {
      $("#" +this.componentSelector).jqxScrollBar("width", arg)
    } else {
      return $("#" +this.componentSelector).jqxScrollBar("width");
    }
  },
  destroy: function () {
    $("#" +this.componentSelector).jqxScrollBar("destroy");  
  },
  isScrolling: function () {
    return $("#" +this.componentSelector).jqxScrollBar("isScrolling");  
  },
  setPosition: function (index) {
    $("#" +this.componentSelector).jqxScrollBar("setPosition", index);  
  },
  render: function () {
    let id = 'jqxScrollBar' + this.generateID() + this.generateID();
    this.componentSelector = id;    return (
      <div id={id}>{this.value ? null : this.props.value}{this.props.children}</div>
    )
  }
});

module.exports = jqxScrollBar;

