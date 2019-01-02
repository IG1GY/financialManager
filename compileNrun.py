import os

task = "None"
with open('info.md', 'r') as readl:
    for line in readl:
        print(line)
        if "compile:" in line:
            task = "compile"
            continue
        elif "run:" in line:
            task = "run"
            continue

        if task == "compile":
            print("compiling: %s" % line)
            os.system('javac --module-path "c:\Program Files\Java\javafx-sdk-11.0.1\lib" --add-modules=javafx.controls,javafx.fxml %s' % line)
        elif task == "run":
            print("running: %s" % line)
            os.system('java --module-path "c:\Program Files\Java\javafx-sdk-11.0.1\lib" --add-modules=javafx.controls,javafx.fxml %s' % line)
