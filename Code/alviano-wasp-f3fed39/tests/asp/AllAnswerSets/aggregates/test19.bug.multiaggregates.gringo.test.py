input = """
3 5 3 4 5 11 12 0 0
1 1 2 2 4 5
1 1 2 1 3 4
1 7 1 1 6
2 13 3 0 3 3 4 12
2 8 3 0 2 7 11 12
2 9 3 0 3 7 11 12
1 1 1 1 10
1 10 2 1 9 8
0
2 a
3 b
4 c
5 d
6 e
7 f
8 aggrGT2
9 aggrGT3
10 g
11 h
12 i
0
B+
0
B-
1
0
1
"""
output = """
{g, aggrGT2, f, i, d, b}
{g, aggrGT2, f, i, d}
{g, aggrGT2, f, h, d}
{g, aggrGT2, f, h, d, b}
{g, aggrGT2, f, h, c, b, d}
{g, aggrGT2, f, h, c, b}
{g, aggrGT2, f, b, c, i}
{g, aggrGT2, f, b, c, i, d}
"""
