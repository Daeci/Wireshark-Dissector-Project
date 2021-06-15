-- trivial postdissector example
-- declare some Fields to be read
ip_src_f = Field.new("ip.src")
ip_dst_f = Field.new("ip.dst")
tcp_src_f = Field.new("tcp.srcport")
tcp_dst_f = Field.new("tcp.dstport")
-- declare our (pseudo) protocol
trivial_proto = Proto("trivial","Trivial Postdissector")
-- create the fields for our "protocol"
src_F = ProtoField.string("trivial.src","Source")
dst_F = ProtoField.string("trivial.dst","Destination")
conv_F = ProtoField.string("trivial.conv","Conversation","A Conversation")
-- add the field to the protocol
trivial_proto.fields = {src_F, dst_F, conv_F}
-- create a function to "postdissect" each frame
function trivial_proto.dissector(buffer,pinfo,tree)
    -- obtain the current values the protocol fields
    local tcp_src = tcp_src_f()
    local tcp_dst = tcp_dst_f()
    local ip_src = ip_src_f()
    local ip_dst = ip_dst_f()
    if tcp_src then
       local subtree = tree:add(trivial_proto,"Trivial Protocol Data")
       local src = tostring(ip_src) .. ":" .. tostring(tcp_src)
       local dst = tostring(ip_dst) .. ":" .. tostring(tcp_dst)
       local conv = src  .. "->" .. dst
       subtree:add(src_F,src)
       subtree:add(dst_F,dst)
       subtree:add(conv_F,conv)
    end
end
-- register our protocol as a postdissector
register_postdissector(trivial_proto)