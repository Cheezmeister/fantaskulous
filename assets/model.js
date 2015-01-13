(function() {
  var root = this;
  
  var Fantaskulous = root.Fantaskulous = {};
  
  var Priority = {
      HIGH:   'B',
      MEDIUM: 'C'
      LOW:    'D'
  };
  
  var Task = Fantaskulous.Task = {
    make: function make(params) {
      var task = {
        description: params.description || '<Do stuff>',
        priority: params.priority || 'MEDIUM',
        completed: params.completed || false,
        guid: params.guid || guid(),
        blocks: params.blocks || [],
        depends: params.depends || []
      };
      
      task.toTodoTxt = function toTodoTxt() {
        var priority = '(' + this.priority + ')';
        var comlete = this.complete ? 'x ' : '';
        var desc = this.description;
        var guid = 'guid:' + this.guid;
        var blocks = 'blocks:' + this.blocks.join(',');
        var depends = 'depends:' + this.depends.join(',');
        
        return [complete priority, desc, guid, blocks, depends].join(' ');
      }
      return task;
    }
  	fromTodoTxt: function(line) {
  		// completion,date,priority,text,projects,contexts,key-value-pairs
		var toTxtRegex = /(x )?(\d{4}-\d{2}-d{2})?(\([A-Z]\) )?(.*)((?: +[\w_]+)+)((?: @[\w_]+)+)
	}
  };
})();