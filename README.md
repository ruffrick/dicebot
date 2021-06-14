[invite]: https://discord.com/api/oauth2/authorize?client_id=846143995189002251&permissions=68672&scope=bot%20applications.commands

<img align="right" src="https://raw.githubusercontent.com/twitter/twemoji/master/assets/svg/1f3b2.svg" height="150" width="150" alt="Dice Logo">

# Dice Discord Bot

A simple Discord bot that provides a D&D-like dice rolling feature.

## Disclaimer

More than anything, this is a platform where I experiment with new Discord features, such as slash commands and message
components. It is currently only running on my local machine while I work on it. No uptime or functionality guaranteed.

## Usage

As of now, there is only one usable command. Depending on if I feel like it and how useful it turns out to be, this list
might expand in the future.

### Commands

- `/roll` Rolls some dice, specified by the arguments: The `dice` argument specifies what sort of and how many dice to
  roll, e.g. `2d6` to roll two six-faced dice, or `d20` to roll a single twenty-faced die. This argument is required.
  The `modifier` argument allows you to add a fixed value on top of the result, the `gm` argument allows you to roll as
  the game master, only showing the result to you. Both of these arguments are optional.

## Invite

Should you want to try it out, you can do so by [inviting the bot to your server][invite]. Remember that at no time are
uptime or functionality guaranteed.
