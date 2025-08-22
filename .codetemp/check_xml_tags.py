import re
p=r"app/src/main/res/layout/fragment_monitor.xml"
with open(p,'r',encoding='utf-8') as f:
    s=f.read()
# find tags
pattern=re.compile(r'<(/?)([a-zA-Z0-9_:\.\-]+)([^>]*)>|<!--|-->|<!\[CDATA\[|\]\]>')
stack=[]
lines=s.splitlines()
index=0
for m in pattern.finditer(s):
    tag=m.group(0)
    is_close = tag.startswith('</')
    is_comment = tag.startswith('<!--')
    is_self_closing = tag.endswith('/>')
    if is_comment:
        continue
    if tag.startswith('<!') or tag.startswith('<?'):
        continue
    if is_close:
        name = tag[2:].split()[0].rstrip('>')
        if not stack or stack[-1][0] != name:
            # compute line number
            lineno = s[:m.start()].count('\n')+1
            print('MISMATCH_CLOSE', name, 'at', lineno, 'expected', stack[-1][0] if stack else 'NONE')
            break
        else:
            stack.pop()
    else:
        name = tag[1:].split()[0].rstrip('>/')
        if not is_self_closing and not tag.startswith('<!'):
            lineno = s[:m.start()].count('\n')+1
            stack.append((name, lineno))
else:
    if stack:
        print('UNCLOSED', stack[-1][0], 'opened at', stack[-1][1])
    else:
        print('ALL MATCH')
