input = """
1 2 2 1 3 4
1 3 2 1 2 4
1 4 0 0
1 5 2 1 6 7
1 6 2 1 5 7
1 7 0 0
1 8 2 1 9 10
1 9 2 1 8 10
1 10 0 0
1 11 2 1 12 13
1 12 2 1 11 13
1 13 0 0
5 15 4 4 2 8 11 2 5 1 3 3 2
1 14 1 0 15
0
8 d
6 not_c
12 not_e
14 ok
5 c
3 not_a
9 not_d
2 a
11 e
0
B+
0
B-
1
0
1
"""
output = """
{a, c, d, e, ok}
{a, c, d, not_e, ok}
{a, c, not_d, e, ok}
{a, c, not_d, not_e, ok}
{a, not_c, d, not_e, ok}
{a, not_c, not_d, e, ok}
{a, not_c, not_d, not_e, ok}
{not_a, c, d, not_e, ok}
{not_a, c, not_d, not_e, ok}
{not_a, not_c, not_d, not_e, ok}
{a, not_c, d, e}
{not_a, c, d, e}
{not_a, c, not_d, e}
{not_a, not_c, d, e}
{not_a, not_c, d, not_e}
{not_a, not_c, not_d, e}
"""
