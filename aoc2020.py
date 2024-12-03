fd = open("/home/dombek/code/my/AdventOfCode/2020-day01.txt")
entries = list(map(int, fd.readlines()))
for ei in entries:
    for ej in entries:
        if ei + ej < 2020:
            for ek in entries:
                if ei + ej + ek == 2020:
                    print(ei * ej * ek)
                    break
