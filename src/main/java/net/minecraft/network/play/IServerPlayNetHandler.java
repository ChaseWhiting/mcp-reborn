package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPlayGameEventPacket;

public interface IServerPlayNetHandler extends INetHandler {
   void handleAnimate(CAnimateHandPacket p_175087_1_);

   void handleChat(CChatMessagePacket p_147354_1_);

   void handleClientCommand(CClientStatusPacket p_147342_1_);

   void handleClientInformation(CClientSettingsPacket p_147352_1_);

   void handleContainerAck(CConfirmTransactionPacket p_147339_1_);

   void handleContainerButtonClick(CEnchantItemPacket p_147338_1_);

   void handleContainerClick(CClickWindowPacket p_147351_1_);

   void handlePlaceRecipe(CPlaceRecipePacket p_194308_1_);

   void handleGameEvent(SPlayGameEventPacket p_147277_1_);

   void handleContainerClose(CCloseWindowPacket p_147356_1_);

   void handleCustomPayload(CCustomPayloadPacket p_147349_1_);

   void handleInteract(CUseEntityPacket p_147340_1_);

   void handleKeepAlive(CKeepAlivePacket p_147353_1_);

   void handleMovePlayer(CPlayerPacket p_147347_1_);

   void handlePlayerAbilities(CPlayerAbilitiesPacket p_147348_1_);

   void handlePlayerAction(CPlayerDiggingPacket p_147345_1_);

   void handleDash(CPlayerDashPacket packet);

   void handlePlayerCommand(CEntityActionPacket p_147357_1_);

   void handlePlayerInput(CInputPacket p_147358_1_);

   void handleSetCarriedItem(CHeldItemChangePacket p_147355_1_);

   void handleSetCreativeModeSlot(CCreativeInventoryActionPacket p_147344_1_);

   void handleSignUpdate(CUpdateSignPacket p_147343_1_);

   void handleUseItemOn(CPlayerTryUseItemOnBlockPacket p_184337_1_);

   void handleUseItem(CPlayerTryUseItemPacket p_147346_1_);

   void handleTeleportToEntityPacket(CSpectatePacket p_175088_1_);

   void handleResourcePackResponse(CResourcePackStatusPacket p_175086_1_);

   void handlePaddleBoat(CSteerBoatPacket p_184340_1_);

   void handleMoveVehicle(CMoveVehiclePacket p_184338_1_);

   void handleAcceptTeleportPacket(CConfirmTeleportPacket p_184339_1_);

   void handleRecipeBookSeenRecipePacket(CMarkRecipeSeenPacket p_191984_1_);

   void handleRecipeBookChangeSettingsPacket(CUpdateRecipeBookStatusPacket p_241831_1_);

   void handleSeenAdvancements(CSeenAdvancementsPacket p_194027_1_);

   void handleCustomCommandSuggestions(CTabCompletePacket p_195518_1_);

   void handleSetCommandBlock(CUpdateCommandBlockPacket p_210153_1_);

   void handleSetCommandMinecart(CUpdateMinecartCommandBlockPacket p_210158_1_);

   void handlePickItem(CPickItemPacket p_210152_1_);

   void handleRenameItem(CRenameItemPacket p_210155_1_);

   void handleSetBeaconPacket(CUpdateBeaconPacket p_210154_1_);

   void handleSetStructureBlock(CUpdateStructureBlockPacket p_210157_1_);

   void handleSelectTrade(CSelectTradePacket p_210159_1_);

   void handleEditBook(CEditBookPacket p_210156_1_);

   void handleEntityTagQuery(CQueryEntityNBTPacket p_211526_1_);

   void handleBlockEntityTagQuery(CQueryTileEntityNBTPacket p_211525_1_);

   void handleSetJigsawBlock(CUpdateJigsawBlockPacket p_217262_1_);

   void handleJigsawGenerate(CJigsawBlockGeneratePacket p_230549_1_);

   void handleChangeDifficulty(CSetDifficultyPacket p_217263_1_);

   void handleLockDifficulty(CLockDifficultyPacket p_217261_1_);
}