# Commands API

zApi allows the creation of commands by using an intuitive DSL syntax, based on a subcommand tree and additional arguments for each subcommand. The syntax has been simplified to be easy to use, read and understand.

Every command is created using the `command` function, which will let you implement your command. A very basic command will look like this:

```kotlin
val hello = command {
	executor { context ->
		context.player?.sendMessage("Hello world!")
	}
}
```

# The `Command` object

This is the main object for each command and subcommand that you will create. It has multiple parameters that you can tweak to configure your command, and several functions used to define what your command does.

Everythink you will see in this section is applicable to both commands and subcommands (but for the sake of readability, we will only speak about commands)

## Command parameters

Each command has a set of parameters used to define some aspects of your command.

Internally, a parameter is just a simple variable that you have to reassign in order to change its value. For example:

```kotlin
val cmd = command {
  permission = "my.command.permission"
  description = "My super useful command"

  // ...
}
```

### Permission (`permission`)

When defining this parameter, the entity running the command will only be able to execute it if they have this permission. By default, no permission is set, allowing anyone to use it.

Its value can be any string, and it doesn't have to be a permission defined in any plugin.yml (although it is recommended that you define all your permissions inside this file)

### Aliases (`aliases`)

Aliases are secondary syntaxes that can be used to run you command when in-game. Aliases does not have to (and should not) contains the main syntax (even if it will not break anything).

Each alias is a simple string. By default, aliases are defined by an empty set. You can then either add aliases to this set, or by reassigning the variable to a new collection (sets are recommended).

### Description (`description`)

The description of a command is a hint to players about what it does when running it. Although not mandatory, it is recommended that you set a description to all commands that you create (even subcommands), because this text can (and will) be used to create help commands ('/help' or '/command help', for example).

## Argument definitions

When running your command, players are able to add arguments to the command. There are two types of arguments :

- Subcommands
- Simple arguments

While the latter is used to add inputs to the command (like a player name, a money amount, or anything you could think of), the former is a whole new command inside your command, letting you define multiple behaviors according to the subcommand the player used.

For example, the '/time' command has two subcommands: 'add' and 'set'. The 'set' command then have one argument, being the time which you want your world to be at.

### Subcommands

Subcommands are, as mentionned previously, a `Command` inside another `Command`, which means that you will be able to do exactly the same things inside a subcommand compared to a commands. It also means that, in theory, you could be able to stack an infinite amount of subcommands inside other subcommands (although it will be hard to run in-game!).

You can define any number of subcommands inside a single command. They are defined by using the `subcommand` function:

```kotlin
val time = command {
  subcommand("set") {
    // Command implementation
  }

  subcommand("add") {
    // Command implementation
  }
}
```

As you can see, a subcommand takes an extra parameter, being its name. You will be able to define aliases, but this name will be its primary syntax.

### Simple arguments

Arguments are your user's input. You can define any number of arguments you want, provided you defined all the previous ones. For example, you can't have an argument 2 without having arguments 0 and 1.

An argument is defined by using the `argument` function:

```kotlin
val hello = command {
  argument(0) {
    // Argument definition
  }
}
```

The number parameter is the index of this argument (starting from 0)

You can find additional informations about the `Argument` object further down.

## Executors

Executors are the actual code pieces that will be executed when a player runs your command. There are multiple executors, each used on specific situations.

Each executor takes at least one parameter, being the context of execution. Some also takes additional parameters that will be explained individually.

They also all have a default implementation, which means that you are not forced to reimplement all of them (although you should always be reimplementing the main executor!).

When you define an executor in a command, it will be passed as the default executor for each subcommands (and subsubcommands, ...). You will then be able to only define a specific executor in your base command, and it will be used in every single subcommand.

### The `Context` object

As said just before, it represents the context of execution. It contains multiple variables that gives you useful informations:

**command**: The `Command` object that was being executed,

**label**: What the user typed, without any arguments,

**args**: The actual arguments of the command,

**sender**: A `CommandSender` instance of who is executing the command,

**player**: A convenient variable set to the sender, if and only if they are a player (otherwise, the variable will be null),

**instant**: When the command was executed.

### Main executor

The main executor is used when everything has gone well and the player hasn't done anything wrong. It is defined by using the `executor` function:

```kotlin
val hello = command {
  executor { ctx ->
    // do something, for example:
    ctx.sender.sendMessage("Hi there!")
  }
}
```

### Syntax executor

This executor is used when the player used the command in a wrong way (meaning one of the arguments is invalid). It takes an additional parameter in the form of the argument index which was wrong (if multiple arguments are invalid, it will always contains the first invalid argument index).

It is defined by using the `syntaxExecutor` function:

```kotlin
val time = command {
  syntaxExecutor { ctx, idx ->
    ctx.sender.sendMessage("Argument number ${idx + 1} is invalid.")
  }
}
```

### Permission executor

This executor is used when the player doesn't have permissions to run this command. It is defined by using the `permissionExecutor` function:

```kotlin
val time = command {
  permission = "time.use"

  permissionExecutor { ctx ->
    ctx.sender.sendMessage("You do not have the required permissions.")
  }
}
```

### Internal error executor

When something goes really bad (and it can happen at any time for any reason), you should inform the player that it has indeed gone very badly. It will be executed if the plugin failed to run your command, or if any of your executors threw an exception. It will take this exception as second argument.

You can define this executor by using the `internalErrorExecutor` function:

```kotlin
val time = command {
  internalErrorExecutor { ctx, exception ->
		ctx.sender.sendMessage("Whoops! Internal error.")
  }
}
```

We just hope this isn't your server burning up.

# The `Argument` object

This object is completely separated from the `Command` object. It has its own set of parameters and functions.

## Argument parameters

Just as for commands, argument parameters are simple variables that you redefine to set their value:

```kotlin
val time = command {
  subcommand("set") {
    argument(0) {
      name = "time"
      required = true
    }
  }
}
```

### Permission (`permission`)

Like commands, arguments can have their own permission to allow their usage. Currently, you can only define a permission for the whole argument. If you need different permissions based on the value of the argument, consider using subcommands instead if possible, or check permissions inside the command executor.

By default, no permission is required to use an argument.

### Required (`required`)

This parameter allows you to define if an argument is required in order to run the command. This is a boolean parameter, and by default, it is set to `true`.

### Long argument (`long`)

Sometimes, you can allow players to write an undefined number of words, usually a text, as their last argument. In order to capture this whole text, you can use the long parameter, which will then return all the remaining text written by the sender, instead of a single word. Be careful, as this must be your last argument, and it cannot be used twice in the same command.

This is a boolean parameter, and by default, it is set to `false`.

## Argument auto-completion (Brigadier)

One of the most praised features of the post-1.13 world is the ability to see what are the possible values when typing a command. This is also known as the Brigadier system. In zApi, subcommands are automatically added to the possible values, but when it comes to arguments, you have to provide the values yourself.

This can be achieved by using two functions.

### Completers

This function is used to provide dynamic values, according to the context, and to the current state of your plugin. Use it when the values you provide can change over time or according to the context (excluding what the player is actually typing).

To create a completer, you use the `completer` function:

```kotlin
val hello = command {
  argument(0) {
    completer { ctx ->
			addAll(Bukkit.getOnlinePlayers().map { it.name })
    }
  }
}
```

### Static completers

Static completers are just like completers, excepting that they cannot change over time. These are to called every time a completion is requested, but only once when setting up the command. Use it to provide static values.

To create a static completer, you use the `staticCompleter` function:

```kotlin
val time = command {
  subcommand("set") {
    argument(0) {
			staticCompleter {
        addAll(
      }
    }
  }
}
```

## Argument filters

By default, completers defines what are the allowed values for your argument. But sometimes, you may want to allow for more values than what you defined in the completers. For example, if you were to allow any integer, it would be a pretty bad idea to create a completer with all possible integer values.

You can work around this problem by adding argument filters. They allow you to test the value in some way, and if your filter returns true, it means that you allow this value to be used.

Creating a filter is done by using the `filter` function:

```kotlin
val time = command {
	subcommand("set") {
    argument(0) {
      filter {
				try {
					it.toInt()
				} catch (ex: NumberFormatException)
					return false
				}
				return true
			}
    }
  }
}
```

# Registering your command

When you are done creating your command, you may want to register it, because otherwise, it will never exist on your server.

Registering a command is extremely easy:

```kotlin
val hello = command {
  // ...
}

// Using a Plugin instance
Commands.register("hello", myPlugin, hello)

// Or by specifying its fallback prefix
Commands.register("hello", "myplugin", hello)
```
