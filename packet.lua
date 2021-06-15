-- trivial protocol example
-- declare our protocol
trivial_proto = Proto("trivial","Trivial Protocol")
-- create a function to dissect it
function trivial_proto.dissector(buffer,pinfo,tree)
    pinfo.cols.protocol = "TRIVIAL"
    local subtree = tree:add(trivial_proto,buffer(),"Trivial Protocol Data")
    subtree:add(buffer(0,2),"The first two bytes: " .. buffer(0,2):uint())
    subtree = subtree:add(buffer(2,2),"The next two bytes")
    subtree:add(buffer(2,1),"The 3rd byte: " .. buffer(2,1):uint())
    subtree:add(buffer(3,1),"The 4th byte: " .. buffer(3,1):uint())
end
-- load the udp.port table
udp_table = DissectorTable.get("udp.port")
-- register our protocol to handle udp port 7777
udp_table:add(7777,trivial_proto)