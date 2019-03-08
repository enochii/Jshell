# simple java shell

based on exercise of the book `Operating System Concepts with Java`

## it supports

1. cd
2. ls
3. history: list the history of your cmd
4. !!: call the latest cmd
5. ! index:call the recent cmd represented by index, u can get information of index by calling the cmd `history`

## notes

1. when we use cmd `cd`, a '/' in the end of `cd`' argument indicates that arg is a relative path. fg, "cd src/".also, u can type "cd ..", "cd absolute-path", "cd ./relativepath/"

## usage

```shell
javac Jsh2.java && java Jsh2
```

and if u wana run it in windows, u can run it in git-bash like me.