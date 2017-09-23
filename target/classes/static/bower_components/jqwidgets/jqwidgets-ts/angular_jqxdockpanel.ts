/*
jQWidgets v4.5.0 (2017-Jan)
Copyright (c) 2011-2017 jQWidgets.
License: http://jqwidgets.com/license/
*/
/// <reference path="jqwidgets.d.ts" />
import { Component, Input, Output, EventEmitter, ElementRef, forwardRef, OnChanges, SimpleChanges } from '@angular/core';
declare let $: any;

@Component({
    selector: 'jqxDockPanel',
    template: '<div><ng-content></ng-content></div>'
})

export class jqxDockPanelComponent implements OnChanges
{
   @Input('disabled') attrDisabled: any;
   @Input('lastchildfill') attrLastchildfill: any;
   @Input('width') attrWidth: any;
   @Input('height') attrHeight: any;

   @Input('auto-create') autoCreate: boolean = true;

   properties: string[] = ['disabled','height','lastchildfill','width'];
   host: any;
   elementRef: ElementRef;
   widgetObject:  jqwidgets.jqxDockPanel;

   constructor(containerElement: ElementRef) {
      this.elementRef = containerElement;
      setTimeout(() => {
         if (this.autoCreate) {
            this.createComponent(); 
         }
      }); 
   }

   ngOnChanges(changes: SimpleChanges) {
      if (this.host) {
         for (let i = 0; i < this.properties.length; i++) {
            let attrName = 'attr' + this.properties[i].substring(0, 1).toUpperCase() + this.properties[i].substring(1);
            let areEqual: boolean;

            if (this[attrName]) {
               if (typeof this[attrName] === 'object') {
                  if (this[attrName] instanceof Array) {
                     areEqual = this.arraysEqual(this[attrName], this.host.jqxDockPanel(this.properties[i]));
                  }
                  if (areEqual) {
                     return false;
                  }

                  this.host.jqxDockPanel(this.properties[i], this[attrName]);
                  continue;
               }

               if (this[attrName] !== this.host.jqxDockPanel(this.properties[i])) {
                  this.host.jqxDockPanel(this.properties[i], this[attrName]); 
               }
            }
         }
      }
   }

   arraysEqual(attrValue: any, hostValue: any): boolean {
      if (attrValue.length != hostValue.length) {
         return false;
      }
      for (let i = 0; i < attrValue.length; i++) {
         if (attrValue[i] !== hostValue[i]) {
            return false;
         }
      }
      return true;
   }

   manageAttributes(): any {
      let options = {};
      for (let i = 0; i < this.properties.length; i++) {
         let attrName = 'attr' + this.properties[i].substring(0, 1).toUpperCase() + this.properties[i].substring(1);
         if (this[attrName] !== undefined) {
            options[this.properties[i]] = this[attrName];
         }
      }
      return options;
   }
   createComponent(options?: any): void {
      if (options) {
         $.extend(options, this.manageAttributes());
      }
      else {
        options = this.manageAttributes();
      }
      this.host = $(this.elementRef.nativeElement.firstChild);
      this.__wireEvents__();
      this.widgetObject = jqwidgets.createInstance(this.host, 'jqxDockPanel', options);
      this.__updateRect__();
   }

   createWidget(options?: any): void {
        this.createComponent(options);
   }

   __updateRect__() : void {
      this.host.css({width: this.attrWidth, height: this.attrHeight});
   }

   setOptions(options: any) : void {
      this.host.jqxDockPanel('setOptions', options);
   }

   // jqxDockPanelComponent properties
   disabled(arg?: boolean) : any {
      if (arg !== undefined) {
          this.host.jqxDockPanel('disabled', arg);
      } else {
          return this.host.jqxDockPanel('disabled');
      }
   }

   height(arg?: jqwidgets.Size) : any {
      if (arg !== undefined) {
          this.host.jqxDockPanel('height', arg);
      } else {
          return this.host.jqxDockPanel('height');
      }
   }

   lastchildfill(arg?: boolean) : any {
      if (arg !== undefined) {
          this.host.jqxDockPanel('lastchildfill', arg);
      } else {
          return this.host.jqxDockPanel('lastchildfill');
      }
   }

   width(arg?: jqwidgets.Size) : any {
      if (arg !== undefined) {
          this.host.jqxDockPanel('width', arg);
      } else {
          return this.host.jqxDockPanel('width');
      }
   }


   // jqxDockPanelComponent functions
   refresh(): void {
      this.host.jqxDockPanel('refresh');
   }


   // jqxDockPanelComponent events
   @Output() onLayout = new EventEmitter();

   __wireEvents__(): void {
      this.host.on('layout', (eventData: any) => { this.onLayout.emit(eventData); });
   }

} //jqxDockPanelComponent


