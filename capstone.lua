-- protocol declaration
capstone_proto = Proto("capstone", "Capstone protocol in LUA")

-- defining a field
capstone_proto.fields.int16 = ProtoField.int16("capstone.sequence_num", "sequenceNum", base.DEC)
capstone_proto.fields.bytes = ProtoField.bytes("capstone.checksum", "checksum")

-- dissector function
function capstone_proto.dissector(buffer, pinfo, tree)
    if buffer:len() < 0 then return end

    pinfo.cols.protocol = capstone_proto.name

    local subtree = tree:add(capstone_proto, buffer(), "Capstone Protocol Data")

    subtree:add(capstone_proto.fields.int16, buffer(0, 2))

    if buffer:len() > 10 then --2+8+n where n > 0
        subtree:add(capstone_proto.fields.bytes, buffer(buffer:len() - 4))
    end
end

local udp_table = DissectorTable.get("udp.port")
udp_table:add(25352, capstone_proto)